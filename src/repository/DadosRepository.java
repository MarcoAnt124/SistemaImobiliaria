package repository;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DadosRepository {
    private static final String ARQUIVO_DADOS = "dados_imobiliaria.json";

    private ArrayList<Vendedor> listaVendedores;
    private ArrayList<Edificio> listaEdificios;
    private ArrayList<Cliente> listaClientes;
    private ArrayList<Venda> listaVendas;

    public DadosRepository() {
        this.listaVendedores = new ArrayList<>();
        this.listaEdificios = new ArrayList<>();
        this.listaClientes = new ArrayList<>();
        this.listaVendas = new ArrayList<>();
        lerArquivo();
    }

    public ArrayList<Cliente> getListaClientes() { return listaClientes; }
    public void setListaClientes(ArrayList<Cliente> listaClientes) { this.listaClientes = listaClientes; }
    public ArrayList<Venda> getListaVendas() { return listaVendas; }
    public void setListaVendas(ArrayList<Venda> listaVendas) { this.listaVendas = listaVendas; }
    public ArrayList<Vendedor> listaVendedores(){ return this.listaVendedores; }
    public ArrayList<Cliente> listaClientes() { return this.listaClientes; }
    public ArrayList<Venda> listaVendas() { return this.listaVendas; }
    public boolean salvarClientes() { return gravarArquivo(); }
    public boolean salvarVendas() { return gravarArquivo(); }
    public ArrayList<Edificio> listaEdificios(){ return this.listaEdificios; }
    public ArrayList<Vendedor> getListaVendedores() { return this.listaVendedores; }
    public ArrayList<Edificio> getListaEdificio() { return this.listaEdificios; }

    public boolean anexarVendedor(Vendedor vendedor) {
        if (vendedor == null) return false;
        for (Vendedor v : this.listaVendedores) {
            if (v.getIdVendedor() == vendedor.getIdVendedor()) {
                return false;
            }
        }
        this.listaVendedores.add(vendedor);
        if (!gravarArquivo()) {
            this.listaVendedores.remove(vendedor);
            return false;
        }
        return true;
    }

    public boolean anexarEdificio(Edificio edificio) {
        if (edificio == null) return false;
        for (Edificio e : this.listaEdificios) {
            if (e.getId() == edificio.getId()) {
                return false;
            }
        }
        this.listaEdificios.add(edificio);
        if (!gravarArquivo()) {
            this.listaEdificios.remove(edificio);
            return false;
        }
        return true;
    }

    public boolean anexarCliente(Cliente cliente){
        if (cliente == null) return false;
        String cpf = validation.Validar.normalizarCpf(cliente.getCpf());
        if (cpf.isEmpty()) return false;
        for (Cliente c : this.listaClientes) {
            if (validation.Validar.normalizarCpf(c.getCpf()).equals(cpf)) {
                return false;
            }
        }
        this.listaClientes.add(cliente);
        if (!gravarArquivo()) {
            this.listaClientes.remove(cliente);
            return false;
        }
        return true;
    }

    public boolean anexarVenda(Venda venda){
        if (venda == null || venda.getApartamento() == null) return false;
        if (existeVendaParaApartamento(venda.getIdEdificio(), venda.getApartamento().getAndar(),
                venda.getApartamento().getNumero())) {
            return false;
        }
        this.listaVendas.add(venda);
        if (!gravarArquivo()) {
            this.listaVendas.remove(venda);
            return false;
        }
        return true;
    }

    public boolean gravarArquivo() {
        try {
            String json = gerarJson();
            Path caminho = Paths.get(ARQUIVO_DADOS);
            Files.writeString(caminho, json, StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao gravar arquivo JSON: " + e.getMessage());
            return false;
        }
    }

    public boolean lerArquivo() {
        Path caminho = Paths.get(ARQUIVO_DADOS);
        if (!Files.exists(caminho)) return true;

        try {
            String json = Files.readString(caminho, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) {
                this.listaVendedores = new ArrayList<>();
                this.listaEdificios = new ArrayList<>();
                this.listaClientes = new ArrayList<>();
                this.listaVendas = new ArrayList<>();
                return true;
            }

            JsonParser parser = new JsonParser(json);
            JsonValue raiz = parser.parse();

            if (!(raiz instanceof JsonObject)) throw new IllegalArgumentException("JSON raiz invalido");

            JsonObject objRaiz = (JsonObject) raiz;

            // IMPORTANTE: Ler clientes ANTES de ler edifícios e vendas para poder vincular
            ArrayList<Vendedor> vendedoresLidos = parseVendedores(objRaiz.getArray("vendedores"));
            int qtdVendedoresAntes = vendedoresLidos.size();
            this.listaVendedores = deduplicarVendedores(vendedoresLidos);
            ArrayList<Cliente> clientesLidos = parseClientes(objRaiz.getArray("clientes"));
            int qtdClientesAntes = clientesLidos.size();
            this.listaClientes = deduplicarClientes(clientesLidos);
            this.listaEdificios = parseEdificios(objRaiz.getArray("edificios"));
            this.listaVendas = parseVendas(objRaiz.getArray("vendas"));

            boolean precisaGravar = qtdVendedoresAntes != this.listaVendedores.size()
                    || qtdClientesAntes != this.listaClientes.size();
            if (!this.listaVendas.isEmpty()) {
                sincronizarVendasComEdificios();
                precisaGravar = true;
            }
            if (precisaGravar && !gravarArquivo()) {
                System.out.println("[AVISO] Dados carregados, mas não foi possível gravar correções no arquivo.");
            }

            return true;
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao ler arquivo JSON. Dados em memoria iniciados vazios.");
            System.out.println("Detalhe: " + e.getMessage());
            this.listaVendedores = new ArrayList<>();
            this.listaEdificios = new ArrayList<>();
            this.listaClientes = new ArrayList<>();
            this.listaVendas = new ArrayList<>();
            return false;
        }
    }

    private String gerarJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"vendedores\":").append(gerarJsonVendedores(this.listaVendedores));
        sb.append(",\"edificios\":").append(gerarJsonEdificios(this.listaEdificios));
        sb.append(",\"clientes\":").append(gerarJsonClientes(this.listaClientes));
        sb.append(",\"vendas\":").append(gerarJsonVendas(this.listaVendas));
        sb.append("}");
        return sb.toString();
    }

    private String gerarJsonVendedores(List<Vendedor> vendedores) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vendedores.size(); i++) {
            Vendedor vendedor = vendedores.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"idVendedor\":").append(vendedor.getIdVendedor()).append(",");
            sb.append("\"nome\":\"").append(escape(vendedor.getNome())).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonEdificios(List<Edificio> edificios) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < edificios.size(); i++) {
            Edificio edificio = edificios.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":").append(edificio.getId()).append(",");
            sb.append("\"nome\":\"").append(escape(edificio.getNome())).append("\",");
            sb.append("\"endereco\":\"").append(escape(edificio.getEndereco())).append("\",");
            EstagioObra estagio = edificio.getEstagioObra() == null ? EstagioObra.LANCAMENTO : edificio.getEstagioObra();
            sb.append("\"estagioObra\":\"").append(estagio.name()).append("\",");
            sb.append("\"andares\":").append(gerarJsonAndares(edificio.getAndares()));
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonClientes(List<Cliente> clientes) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            if (i > 0) sb.append(",");

            sb.append("{");
            sb.append("\"nome\":\"").append(escape(cliente.getNome())).append("\",");
            sb.append("\"cpf\":\"").append(escape(cliente.getCpf())).append("\",");
            sb.append("\"rg\":\"").append(escape(cliente.getRg())).append("\",");
            String estadoCivilNome = (cliente.getEstadoCivil() == null ? EstadoCivil.SOLTEIRO : cliente.getEstadoCivil()).name();
            sb.append("\"estadoCivil\":\"").append(estadoCivilNome).append("\"");

            if (cliente.getConjuge() != null) {
                Conjuge conjuge = cliente.getConjuge();
                sb.append(",\"conjuge\":{");
                sb.append("\"nome\":\"").append(escape(conjuge.getNome())).append("\",");
                sb.append("\"cpf\":\"").append(escape(conjuge.getCpf())).append("\",");
                sb.append("\"rg\":\"").append(escape(conjuge.getRg())).append("\"");
                sb.append("}");
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonVendas(List<Venda> vendas) {
        StringBuilder sb = new StringBuilder("[");
        boolean primeiro = true;
        for (int i = 0; i < vendas.size(); i++) {
            Venda venda = vendas.get(i);
            if (venda == null || venda.getVendedor() == null || venda.getCliente() == null || venda.getApartamento() == null) {
                continue;
            }

            if (!primeiro) sb.append(",");
            primeiro = false;

            sb.append("{");
            sb.append("\"vendedor\":{");
            sb.append("\"idVendedor\":").append(venda.getVendedor().getIdVendedor()).append(",");
            sb.append("\"nome\":\"").append(escape(venda.getVendedor().getNome())).append("\"");
            sb.append("},");

            sb.append("\"cliente\":{");
            sb.append("\"nome\":\"").append(escape(venda.getCliente().getNome())).append("\",");
            sb.append("\"cpf\":\"").append(escape(venda.getCliente().getCpf())).append("\",");
            sb.append("\"rg\":\"").append(escape(venda.getCliente().getRg())).append("\",");
            String estadoCivilVendaNome = (venda.getCliente().getEstadoCivil() == null ? EstadoCivil.SOLTEIRO : venda.getCliente().getEstadoCivil()).name();
            sb.append("\"estadoCivil\":\"").append(estadoCivilVendaNome).append("\"");

            if (venda.getCliente().getConjuge() != null) {
                Conjuge conjuge = venda.getCliente().getConjuge();
                sb.append(",\"conjuge\":{");
                sb.append("\"nome\":\"").append(escape(conjuge.getNome())).append("\",");
                sb.append("\"cpf\":\"").append(escape(conjuge.getCpf())).append("\",");
                sb.append("\"rg\":\"").append(escape(conjuge.getRg())).append("\"");
                sb.append("}");
            }
            sb.append("},");

            Apartamento apt = venda.getApartamento();
            sb.append("\"apartamento\":{");
            sb.append("\"numero\":").append(apt.getNumero()).append(",");
            sb.append("\"andar\":").append(apt.getAndar()).append(",");
            sb.append("\"metragem\":").append(apt.getMetragem()).append(",");
            sb.append("\"quantidadeDeQuartos\":").append(apt.getQuantidadeDeQuartos()).append(",");
            sb.append("\"quantidadeDeBanheiros\":").append(apt.getQuantidadeDeBanheiros()).append(",");
            sb.append("\"valorDeVenda\":").append(apt.getValorDeVenda()).append(",");
            sb.append("\"valorSinal\":").append(apt.getValorSinal()).append(",");

            // CORREÇÃO: Pega o CPF do objeto Cliente e salva como String no JSON
            String cpfInteressado = (apt.getClienteInteressado() != null) ? apt.getClienteInteressado().getCpf() : "";
            sb.append("\"cpfInteressado\":\"").append(escape(cpfInteressado)).append("\",");

            StatusApartamento aptStatus = (apt.getStatus() == null ? StatusApartamento.DISPONIVEL : apt.getStatus());
            sb.append("\"status\":\"").append(aptStatus.name()).append("\"");
            sb.append("},");

            String data = venda.getDataDaVenda() == null ? "" : venda.getDataDaVenda().toString();
            sb.append("\"dataDaVenda\":\"").append(escape(data)).append("\",");
            sb.append("\"idEdificio\":").append(venda.getIdEdificio()).append(",");
            sb.append("\"valorFinal\":").append(venda.getValorFinal());

            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonAndares(List<Andar> andares) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < andares.size(); i++) {
            Andar andar = andares.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"numero\":").append(andar.getNumero()).append(",");
            sb.append("\"quantidadeDeApartamentos\":").append(andar.getQuantidadeDeApartamentos()).append(",");
            sb.append("\"apartamentos\":").append(gerarJsonApartamentos(andar.getApartamentos()));
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String gerarJsonApartamentos(List<Apartamento> apartamentos) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < apartamentos.size(); i++) {
            Apartamento apt = apartamentos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"numero\":").append(apt.getNumero()).append(",");
            sb.append("\"andar\":").append(apt.getAndar()).append(",");
            sb.append("\"metragem\":").append(apt.getMetragem()).append(",");
            sb.append("\"quantidadeDeQuartos\":").append(apt.getQuantidadeDeQuartos()).append(",");
            sb.append("\"quantidadeDeBanheiros\":").append(apt.getQuantidadeDeBanheiros()).append(",");
            sb.append("\"valorDeVenda\":").append(apt.getValorDeVenda()).append(",");
            sb.append("\"valorSinal\":").append(apt.getValorSinal()).append(",");

            // CORREÇÃO: Pega o CPF do objeto Cliente e salva como String no JSON
            String cpfInteressado = (apt.getClienteInteressado() != null) ? apt.getClienteInteressado().getCpf() : "";
            sb.append("\"cpfInteressado\":\"").append(escape(cpfInteressado)).append("\",");

            StatusApartamento statusApt = apt.getStatus() == null ? StatusApartamento.DISPONIVEL : apt.getStatus();
            sb.append("\"status\":\"").append(statusApt.name()).append("\"");
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private ArrayList<Cliente> deduplicarClientes(ArrayList<Cliente> lista) {
        ArrayList<Cliente> unicos = new ArrayList<>();
        for (Cliente c : lista) {
            if (c == null) continue;
            String cpf = validation.Validar.normalizarCpf(c.getCpf());
            if (cpf.isEmpty()) continue;
            boolean duplicado = false;
            for (Cliente u : unicos) {
                if (validation.Validar.normalizarCpf(u.getCpf()).equals(cpf)) {
                    duplicado = true;
                    break;
                }
            }
            if (!duplicado) {
                unicos.add(c);
            }
        }
        return unicos;
    }

    public boolean existeVendaParaApartamento(int idEdificio, int numAndar, int numApt) {
        for (Venda v : this.listaVendas) {
            if (v == null || v.getApartamento() == null) continue;
            Apartamento aptV = v.getApartamento();
            if (aptV.getNumero() != numApt || aptV.getAndar() != numAndar) {
                continue;
            }
            int idVenda = v.getIdEdificio();
            if (idEdificio > 0 && idVenda > 0) {
                if (idEdificio == idVenda) return true;
            } else if (idEdificio > 0 && idVenda <= 0) {
                Edificio unico = buscarUnicoEdificioPorApartamento(numAndar, numApt);
                if (unico != null && unico.getId() == idEdificio) return true;
            } else if (idEdificio <= 0 && idVenda <= 0) {
                return true;
            }
        }
        return false;
    }

    private Edificio buscarUnicoEdificioPorApartamento(int numAndar, int numApt) {
        Edificio encontrado = null;
        for (Edificio ed : this.listaEdificios) {
            if (!edificioContemApartamento(ed, numAndar, numApt)) continue;
            if (encontrado != null) return null;
            encontrado = ed;
        }
        return encontrado;
    }

    private boolean edificioContemApartamento(Edificio ed, int numAndar, int numApt) {
        for (Andar andar : ed.getAndares()) {
            if (andar.getNumero() != numAndar) continue;
            for (Apartamento apt : andar.getApartamentos()) {
                if (apt.getNumero() == numApt) return true;
            }
        }
        return false;
    }

    private ArrayList<Vendedor> deduplicarVendedores(ArrayList<Vendedor> lista) {
        ArrayList<Vendedor> unicos = new ArrayList<>();
        for (Vendedor v : lista) {
            boolean duplicado = false;
            for (Vendedor u : unicos) {
                if (u.getIdVendedor() == v.getIdVendedor()) {
                    duplicado = true;
                    break;
                }
            }
            if (!duplicado) {
                unicos.add(v);
            }
        }
        return unicos;
    }

    private ArrayList<Vendedor> parseVendedores(JsonArray arr) {
        ArrayList<Vendedor> resultado = new ArrayList<>();
        if (arr == null) return resultado;

        for (JsonValue item : arr.values) {
            JsonObject obj = asObject(item);
            int id = obj.getInt("idVendedor", 0);
            String nome = obj.getString("nome", "");
            resultado.add(new Vendedor(id, nome));
        }
        return resultado;
    }

    private ArrayList<Edificio> parseEdificios(JsonArray arr) {
        ArrayList<Edificio> resultado = new ArrayList<>();
        if (arr == null) return resultado;

        for (JsonValue item : arr.values) {
            JsonObject objEdificio = asObject(item);
            int id = objEdificio.getInt("id", 0);
            if (id <= 0) continue;
            String nome = objEdificio.getString("nome", "");
            String endereco = objEdificio.getString("endereco", "");

            Edificio edificio = new Edificio(id, nome, endereco);
            String estagioStr = objEdificio.getString("estagioObra", EstagioObra.LANCAMENTO.name());
            try {
                edificio.setEstagioObra(EstagioObra.valueOf(estagioStr));
            } catch (IllegalArgumentException ignored) {
                edificio.setEstagioObra(EstagioObra.LANCAMENTO);
            }
            JsonArray andaresArr = objEdificio.getArray("andares");
            if (andaresArr != null) {
                for (JsonValue andarValue : andaresArr.values) {
                    JsonObject objAndar = asObject(andarValue);
                    int numeroAndar = objAndar.getInt("numero", 0);
                    if (numeroAndar <= 0) continue;
                    Andar andar = new Andar(numeroAndar);

                    JsonArray aptosArr = objAndar.getArray("apartamentos");
                    if (aptosArr != null) {
                        for (JsonValue aptValue : aptosArr.values) {
                            JsonObject objApt = asObject(aptValue);
                            Apartamento apt = new Apartamento(
                                    objApt.getInt("numero", 0),
                                    objApt.getInt("andar", numeroAndar),
                                    objApt.getDouble("metragem", 0.0),
                                    objApt.getInt("quantidadeDeQuartos", 0),
                                    objApt.getInt("quantidadeDeBanheiros", 0),
                                    objApt.getDouble("valorDeVenda", 0.0)
                            );
                            apt.setValorSinal(objApt.getDouble("valorSinal", 0.0));

                            // CORREÇÃO: Lê o CPF em String e converte para o Objeto Cliente correto da lista
                            String cpfSalvo = objApt.getString("cpfInteressado", "");
                            apt.setClienteInteressado(buscarClientePorCpf(cpfSalvo));

                            String status = objApt.getString("status", StatusApartamento.DISPONIVEL.name());
                            try { apt.setStatus(StatusApartamento.valueOf(status)); }
                            catch (IllegalArgumentException ignored) { apt.setStatus(StatusApartamento.DISPONIVEL); }
                            normalizarApartamentoInconsistente(apt);
                            andar.getApartamentos().add(apt);
                        }
                    }
                    edificio.getAndares().add(andar);
                }
            }
            resultado.add(edificio);
        }
        return resultado;
    }

    private ArrayList<Cliente> parseClientes(JsonArray arr) {
        ArrayList<Cliente> resultado = new ArrayList<>();
        if (arr == null) return resultado;

        for (JsonValue item : arr.values) {
            JsonObject obj = asObject(item);
            String nome = obj.getString("nome", "");
            String cpf = obj.getString("cpf", "");
            String rg = obj.getString("rg", "");
            String estadoCivilStr = obj.getString("estadoCivil", EstadoCivil.SOLTEIRO.name());
            EstadoCivil estadoCivil;
            try { estadoCivil = EstadoCivil.valueOf(estadoCivilStr); }
            catch (IllegalArgumentException ignored) { estadoCivil = EstadoCivil.SOLTEIRO; }

            JsonObject objConjuge = obj.getObject("conjuge");
            if (objConjuge != null) {
                Conjuge conjuge = new Conjuge(
                        objConjuge.getString("nome", ""),
                        objConjuge.getString("cpf", ""),
                        objConjuge.getString("rg", "")
                );
                resultado.add(new Cliente(nome, cpf, rg, estadoCivil, conjuge));
            } else {
                resultado.add(new Cliente(nome, cpf, rg, estadoCivil));
            }
        }
        return resultado;
    }

    private ArrayList<Venda> parseVendas(JsonArray arr) {
        ArrayList<Venda> resultado = new ArrayList<>();
        if (arr == null) return resultado;

        int ignoradas = 0;
        for (JsonValue item : arr.values) {
            JsonObject obj = asObject(item);

            JsonObject objVendedor = obj.getObject("vendedor");
            JsonObject objCliente = obj.getObject("cliente");
            JsonObject objApartamento = obj.getObject("apartamento");

            if (objVendedor == null || objCliente == null || objApartamento == null) continue;

            Vendedor vendedor = new Vendedor(
                    objVendedor.getInt("idVendedor", 0),
                    objVendedor.getString("nome", "")
            );

            String cpfCli = objCliente.getString("cpf", "");
            Cliente cliente = null;
            String cpfCliNorm = validation.Validar.normalizarCpf(cpfCli);
            for (Cliente c : this.listaClientes) {
                if (c.getCpf() != null && validation.Validar.normalizarCpf(c.getCpf()).equals(cpfCliNorm)) {
                    cliente = c;
                    break;
                }
            }
            if (cliente == null) {
                ignoradas++;
                continue;
            }

            int idEdificio = obj.getInt("idEdificio", 0);

            Apartamento apt = new Apartamento(
                    objApartamento.getInt("numero", 0),
                    objApartamento.getInt("andar", 0),
                    objApartamento.getDouble("metragem", 0.0),
                    objApartamento.getInt("quantidadeDeQuartos", 0),
                    objApartamento.getInt("quantidadeDeBanheiros", 0),
                    objApartamento.getDouble("valorDeVenda", 0.0)
            );
            apt.setValorSinal(objApartamento.getDouble("valorSinal", 0.0));

            // CORREÇÃO: Lê o CPF em String e converte para Objeto Cliente
            String cpfSalvoApt = objApartamento.getString("cpfInteressado", "");
            apt.setClienteInteressado(buscarClientePorCpf(cpfSalvoApt));

            String status = objApartamento.getString("status", StatusApartamento.DISPONIVEL.name());
            try { apt.setStatusApartamento(StatusApartamento.valueOf(status)); }
            catch (IllegalArgumentException ignored) { apt.setStatusApartamento(StatusApartamento.DISPONIVEL); }

            String dataStr = obj.getString("dataDaVenda", "");
            LocalDate dataDaVenda;
            try { dataDaVenda = dataStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dataStr); }
            catch (Exception ignored) { dataDaVenda = LocalDate.now(); }

            double valorFinal = obj.getDouble("valorFinal", 0.0);
            resultado.add(new Venda(vendedor, apt, cliente, idEdificio, dataDaVenda, valorFinal));
        }
        if (ignoradas > 0) {
            System.out.println("[AVISO] " + ignoradas + " venda(s) ignorada(s) no JSON (cliente não cadastrado ou dados incompletos).");
        }
        return resultado;
    }

    private void normalizarApartamentoInconsistente(Apartamento apt) {
        if (apt.getStatus() == StatusApartamento.DISPONIVEL && apt.getValorSinal() > 0) {
            apt.setValorSinal(0);
            apt.setClienteInteressado(null);
        }
    }

    private Cliente buscarClientePorCpf(String cpf) {
        String cpfNorm = validation.Validar.normalizarCpf(cpf);
        if (cpfNorm.isEmpty()) {
            return null;
        }
        for (Cliente c : this.listaClientes) {
            if (validation.Validar.normalizarCpf(c.getCpf()).equals(cpfNorm)) {
                return c;
            }
        }
        return null;
    }

    private void sincronizarVendasComEdificios() {
        for (Venda venda : this.listaVendas) {
            if (venda == null || venda.getApartamento() == null) {
                continue;
            }
            int numApt = venda.getApartamento().getNumero();
            int numAndar = venda.getApartamento().getAndar();
            int idEdificio = venda.getIdEdificio();

            Edificio alvo = null;
            if (idEdificio > 0) {
                for (Edificio ed : this.listaEdificios) {
                    if (ed.getId() == idEdificio) {
                        alvo = ed;
                        break;
                    }
                }
            } else {
                alvo = buscarUnicoEdificioPorApartamento(numAndar, numApt);
            }
            if (alvo == null) continue;
            marcarApartamentoVendidoNoEdificio(alvo, numAndar, numApt);
        }
    }

    private void marcarApartamentoVendidoNoEdificio(Edificio edificio, int numAndar, int numApt) {
        for (Andar andar : edificio.getAndares()) {
            if (andar.getNumero() != numAndar) continue;
            for (Apartamento apt : andar.getApartamentos()) {
                if (apt.getNumero() == numApt) {
                    apt.setStatusApartamento(StatusApartamento.VENDIDO);
                    apt.setClienteInteressado(null);
                    apt.setValorSinal(0);
                    return;
                }
            }
        }
    }

    private JsonObject asObject(JsonValue value) {
        if (!(value instanceof JsonObject)) throw new IllegalArgumentException("Item esperado como objeto JSON");
        return (JsonObject) value;
    }

    private String escape(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private interface JsonValue {}
    private static class JsonString implements JsonValue { private final String value; private JsonString(String value) { this.value = value; } }
    private static class JsonNumber implements JsonValue { private final double value; private JsonNumber(double value) { this.value = value; } }
    private static class JsonArray implements JsonValue { private final ArrayList<JsonValue> values = new ArrayList<>(); }

    private static class JsonObject implements JsonValue {
        private final java.util.HashMap<String, JsonValue> values = new java.util.HashMap<>();
        private String getString(String key, String defaultValue) { JsonValue value = values.get(key); if (value instanceof JsonString) return ((JsonString) value).value; return defaultValue; }
        private int getInt(String key, int defaultValue) { JsonValue value = values.get(key); if (value instanceof JsonNumber) return (int) ((JsonNumber) value).value; return defaultValue; }
        private double getDouble(String key, double defaultValue) { JsonValue value = values.get(key); if (value instanceof JsonNumber) return ((JsonNumber) value).value; return defaultValue; }
        private JsonArray getArray(String key) { JsonValue value = values.get(key); if (value instanceof JsonArray) return (JsonArray) value; return null; }
        private JsonObject getObject(String key) { JsonValue value = values.get(key); if (value instanceof JsonObject) return (JsonObject) value; return null; }
    }

    private static class JsonParser {
        private final String text;
        private int pos;
        private JsonParser(String text) { this.text = text; this.pos = 0; }
        private JsonValue parse() { skipSpaces(); JsonValue value = parseValue(); skipSpaces(); if (pos != text.length()) throw new IllegalArgumentException("Conteudo extra apos JSON valido"); return value; }

        private JsonValue parseValue() {
            skipSpaces();
            if (pos >= text.length()) throw new IllegalArgumentException("JSON inesperadamente finalizado");
            char c = text.charAt(pos);
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return new JsonString(parseString());
            if (c == '-' || Character.isDigit(c)) return new JsonNumber(parseNumber());
            throw new IllegalArgumentException("Valor JSON invalido na posicao " + pos);
        }

        private JsonObject parseObject() {
            expect('{'); JsonObject obj = new JsonObject(); skipSpaces();
            if (peek('}')) { expect('}'); return obj; }
            while (true) {
                skipSpaces(); String key = parseString(); skipSpaces(); expect(':'); skipSpaces();
                JsonValue value = parseValue(); obj.values.put(key, value); skipSpaces();
                if (peek('}')) { expect('}'); break; }
                expect(',');
            }
            return obj;
        }

        private JsonArray parseArray() {
            expect('['); JsonArray arr = new JsonArray(); skipSpaces();
            if (peek(']')) { expect(']'); return arr; }
            while (true) {
                skipSpaces(); arr.values.add(parseValue()); skipSpaces();
                if (peek(']')) { expect(']'); break; }
                expect(',');
            }
            return arr;
        }

        private String parseString() {
            expect('"'); StringBuilder sb = new StringBuilder();
            while (pos < text.length()) {
                char c = text.charAt(pos++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    if (pos >= text.length()) throw new IllegalArgumentException("Escape invalido em string JSON");
                    char e = text.charAt(pos++);
                    switch (e) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        default: throw new IllegalArgumentException("Escape nao suportado: \\" + e);
                    }
                } else { sb.append(c); }
            }
            throw new IllegalArgumentException("String JSON nao finalizada");
        }

        private double parseNumber() {
            int start = pos;
            if (peek('-')) pos++;
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) pos++;
            if (pos < text.length() && text.charAt(pos) == '.') {
                pos++;
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) pos++;
            }
            String numero = text.substring(start, pos);
            try { return Double.parseDouble(numero); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Numero JSON invalido: " + numero); }
        }

        private void skipSpaces() { while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++; }
        private boolean peek(char expected) { return pos < text.length() && text.charAt(pos) == expected; }
        private void expect(char expected) { if (pos >= text.length() || text.charAt(pos) != expected) throw new IllegalArgumentException("Esperado '" + expected + "' na posicao " + pos); pos++; }
    }
}