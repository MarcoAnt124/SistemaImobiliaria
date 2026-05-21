package service;
import model.*;
import repository.DadosRepository;
import validation.Validar;

import java.util.ArrayList;

public class ImobiliariaService {
    private DadosRepository dados;
    private int IDgenerator;

    public ImobiliariaService(DadosRepository dados) {
        this.dados = dados;
        this.IDgenerator = buscaIdInicial()+1;
    }

    public int getIDgenerator() {
        return IDgenerator;
    }

    public void setIDgenerator(int IDgenerator) {
        this.IDgenerator = IDgenerator;
    }

    public boolean verificarStatusDisponivel(Apartamento apt){
        return apt.getStatus() == StatusApartamento.DISPONIVEL;
    }

    public Edificio procurarEdificioApartamento(Apartamento apt){
        for(Edificio edAtual : dados.listaEdificios()){
            for(Andar andarAtual : edAtual.getAndares()){
                for(Apartamento aptAtual : andarAtual.getApartamentos()){
                    if(aptAtual == apt){
                        return edAtual;
                    }
                }
            }
        }
        return null;
    }

    public String gerarListaVendas(){
        ArrayList<Venda> listaVendas = dados.getListaVendas();
        StringBuilder sb = new StringBuilder();

        double somaTotal = 0.0;

        sb.append("\n====================================================================================================\n");
        sb.append(String.format("%65s\n", "RELATÓRIO HISTÓRICO DE VENDAS"));
        sb.append("====================================================================================================\n");
        sb.append(" DATA       | VENDEDOR        | IMÓVEL (APTO) | CLIENTE              | VALOR FINAL\n");
        sb.append("------------|-----------------|---------------|----------------------|------------------------------\n");

        String mask = " %-10s | %-15s | Apto %-8d | %-20s | R$ %,-12.2f\n";
        int totalExibidas = 0;

        for (Venda vendaAtual : listaVendas) {
            if (vendaAtual == null || vendaAtual.getVendedor() == null
                    || vendaAtual.getCliente() == null || vendaAtual.getApartamento() == null) {
                continue;
            }
            Apartamento aptRef = vendaAtual.getApartamento();
            Apartamento aptAoVivo = buscarApartamentoCadastrado(
                    vendaAtual.getIdEdificio(), aptRef.getAndar(), aptRef.getNumero());
            if (aptAoVivo != null) {
                aptRef = aptAoVivo;
            }

            String dataFormatada = vendaAtual.getDataDaVenda() == null
                    ? "-"
                    : vendaAtual.getDataDaVenda().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String nomeCli = vendaAtual.getCliente().getNome();
            if (nomeCli == null || nomeCli.isEmpty()) {
                nomeCli = "-";
            } else if (nomeCli.length() > 20) {
                nomeCli = nomeCli.substring(0, 17) + "...";
            }

            String nomeVend = vendaAtual.getVendedor().getNome();
            if (nomeVend == null) nomeVend = "-";

            sb.append(String.format(new java.util.Locale("pt", "BR"), mask,
                    dataFormatada,
                    nomeVend,
                    aptRef.getNumero(),
                    nomeCli,
                    vendaAtual.getValorFinal()
            ));

            somaTotal += vendaAtual.getValorFinal();
            totalExibidas++;
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(new java.util.Locale("pt", "BR"),
                " TOTAL DE VENDAS: %-5d | VOLUME FINANCEIRO TOTAL: R$ %,-20.2f\n",
                totalExibidas, somaTotal));
        sb.append("====================================================================================================\n");

        return sb.toString();
    }

    public DadosRepository getDados(){
        return this.dados;
    }

    private Apartamento buscarApartamentoCadastrado(int idEdificio, int andar, int numero) {
        Edificio ed = buscarEdificioPorId(idEdificio);
        if (ed == null) return null;
        Andar an = buscarAndarNoEdificio(ed, andar);
        if (an == null) return null;
        return buscarApartamentoNoAndar(an, numero);
    }

    public String gerarRelatorioTotal(ArrayList<Edificio> listaEdificio){
        StringBuilder builder = new StringBuilder();
        builder.append("======================================================================\n");
        builder.append(String.format("%70s\n", "LISTA DE EDIFICIOS DISPONÍVEIS\n"));
        builder.append("======================================================================\n");
            for(Edificio edificioAtual : listaEdificio){
                String nomeEd = edificioAtual.getNome() == null ? "-" : edificioAtual.getNome().toUpperCase();
                builder.append("\nEdifício: ").append(nomeEd);
                builder.append("\nEndereço: ").append(edificioAtual.getEndereco() == null ? "-" : edificioAtual.getEndereco()).append("\n");

                builder.append(gerarRelatorioBase(edificioAtual));

                builder.append("\n");
            }
        return builder.toString();
    }

    public String gerarRelatorioBase(Edificio edificio) {
        StringBuilder sb = new StringBuilder();
        int contador = 0;

        sb.append("\n").append("=".repeat(70)).append("\n");
        String nomeEd = edificio.getNome() == null ? "-" : edificio.getNome().toUpperCase();
        sb.append(String.format("%45s\n", nomeEd));
        sb.append("=".repeat(70)).append("\n");

        sb.append("  NÚMERO  |  ANDAR  |  ÁREA (m²)  |  VALOR (R$)    |  STATUS\n");
        sb.append("-".repeat(70)).append("\n");

        for (Andar andarAtual : edificio.getAndares()) {
            for (Apartamento aptAtual : andarAtual.getApartamentos()) {
                StatusApartamento status = aptAtual.getStatus();

                if (status == StatusApartamento.DISPONIVEL || status == StatusApartamento.RESERVADO) {
                    contador++;

                    sb.append(String.format(new java.util.Locale("pt", "BR"),
                            "  %-8d |  %-6s |  %-10.1f | R$ %,-12.2f | %s\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero() + "º",
                            aptAtual.getMetragem(),
                            aptAtual.getValorDeVenda(),
                            status.name()
                    ));
                }
            }
        }

        sb.append("-".repeat(70)).append("\n");
        sb.append(" Total encontrado: ").append(contador).append(" unidades.\n");
        sb.append("======================================================================\n");

        return sb.toString();
    }

    public Edificio iniciarNovoEdificio(int id, String nome, String endereco){
        return new Edificio(id, nome, endereco);
    }

    public int gerarNumApartamento(Andar andarApt){

        int numPrevisto = (100 * andarApt.getNumero());

        if(andarApt.getApartamentos().isEmpty()){
            return numPrevisto+1;
        } else {
            int maiorNumApt = 0;
            for(Apartamento aptAtual : andarApt.getApartamentos()){
                if(aptAtual.getNumero() > maiorNumApt) maiorNumApt = aptAtual.getNumero();
            }
            return maiorNumApt+1;
        }
    }

    // Busca o andar; se não achar, cria um novo e pendura o apto nele
    public boolean vincularApartamento(Edificio edificio, int numAndar,double area, double preco, int qtdQuartos, int qtdBanheiros){
        if (edificio == null || numAndar <= 0) {
            return false;
        }
        Andar novoAndar = null;
        for(Andar andarAtual : edificio.getAndares()){
            if(andarAtual.getNumero() == numAndar){
                novoAndar = andarAtual;
                break;
            }
        }

        if(novoAndar == null){
            novoAndar = new Andar(numAndar);
            edificio.getAndares().add(novoAndar);
        }

        int numApt = gerarNumApartamento(novoAndar);

        Apartamento novoApt = new Apartamento(numApt, novoAndar.getNumero(), area,qtdQuartos, qtdBanheiros, preco);
        novoAndar.getApartamentos().add(novoApt);
        return true;
    }

    public boolean removerUltimoApartamento(Edificio edificio, int numAndar, int numApt) {
        Andar andar = buscarAndarNoEdificio(edificio, numAndar);
        if (andar == null) return false;
        Apartamento apt = buscarApartamentoNoAndar(andar, numApt);
        if (apt == null) return false;
        return andar.getApartamentos().remove(apt);
    }

    public boolean salvarEdificio(Edificio edificio){
        if (edificio == null
                || !Validar.textoNaoVazio(edificio.getNome())
                || !Validar.textoNaoVazio(edificio.getEndereco())
                || buscarEdificioPorId(edificio.getId()) != null) {
            return false;
        }
        return dados.anexarEdificio(edificio);
    }

    public String gerarListaSimplesEdificios(){
        ArrayList<Edificio> Edificios = dados.getListaEdificio();
        StringBuilder sb = new StringBuilder();

        sb.append("======================================================================\n");
        sb.append(String.format(" %-4s | %-25s | %-20s | %s\n", "ID", "NOME", "ENDEREÇO", "ESTÁGIO"));
        sb.append("----------------------------------------------------------------------\n");

        for(Edificio edificioAtual : Edificios){
            sb.append(String.format(" %03d  | %-25s | %-20s | %s\n",
                    edificioAtual.getId(),
                    edificioAtual.getNome(),
                    edificioAtual.getEndereco(),
                    edificioAtual.getEstagioObra()));
        }

        sb.append("======================================================================\n");
        return sb.toString();
        }

    public Edificio buscarEdificioPorId(int idEdificio){
        ArrayList<Edificio> listaEd = dados.getListaEdificio();
        Edificio edificioSelecionado = null;

        for(Edificio atual : listaEd){
            if(atual.getId() == idEdificio) {
                edificioSelecionado = atual;
                break;
            }
        }

        return edificioSelecionado;
    }

    public Andar buscarAndarNoEdificio(Edificio edificio, int numAndar){
        Andar andarSelecionado = null;
        for(Andar andarAtual : edificio.getAndares()){
            if(andarAtual.getNumero() == numAndar){
                andarSelecionado = andarAtual;
                break;
            }
        }
        return andarSelecionado;
    }

    public Apartamento buscarApartamentoNoAndar(Andar andar, int numApt){
        Apartamento aptSelecionado = null;
        for(Apartamento aptAtual : andar.getApartamentos()){
            if(aptAtual.getNumero() == numApt){
               aptSelecionado = aptAtual;
               break;
            }
        }
        return aptSelecionado;
    }

    public boolean atualizarStatus(Apartamento apt, int op, Cliente clienteInteressado){
        if (apt == null || apt.getStatus() == StatusApartamento.VENDIDO) {
            return false;
        }
        if (op != 1 && op != 2) {
            return false;
        }
        if (op == 2 && clienteInteressado == null) {
            return false;
        }
        if (op == 2 && apt.getStatus() == StatusApartamento.RESERVADO) {
            return false;
        }

        if ((op == 1)) {
            apt.setStatus(StatusApartamento.DISPONIVEL);
            apt.setClienteInteressado(null);
            apt.setValorSinal(0);
        } else {
            apt.setStatus(StatusApartamento.RESERVADO);
            apt.setClienteInteressado(clienteInteressado);
        }

        return true;
    }

    public boolean adicionarVendedor(String nome, int id){
        if (!Validar.textoNaoVazio(nome) || buscarVendedorPorId(id) != null) {
            return false;
        }
        Vendedor novoVendedor = new Vendedor(id, nome.trim());
        return dados.anexarVendedor(novoVendedor);
    }

    public Vendedor buscarVendedorPorId(int id) {
        for (Vendedor v : dados.listaVendedores()) {
            if (v.getIdVendedor() == id) {
                return v;
            }
        }
        return null;
    }

    public boolean adicionarCliente(Cliente cliente){
        if (cliente == null
                || !Validar.textoNaoVazio(cliente.getNome())
                || !Validar.textoNaoVazio(cliente.getCpf())
                || buscaCliente(cliente.getCpf()) != null) {
            return false;
        }
        return dados.anexarCliente(cliente);
    }

    private boolean adicionarVenda(Venda venda){
        return(dados.anexarVenda(venda));
    }

    public Cliente buscaCliente(String dadoCliente){
        if (dadoCliente == null) {
            return null;
        }
        String busca = dadoCliente.trim();
        String buscaCpf = Validar.normalizarCpf(busca);
        ArrayList<Cliente> listaCliente = dados.getListaClientes();

        if (!buscaCpf.isEmpty()) {
            for (Cliente clienteAtual : listaCliente) {
                if (Validar.normalizarCpf(clienteAtual.getCpf()).equals(buscaCpf)) {
                    return clienteAtual;
                }
                if (clienteAtual.getCpf() != null && clienteAtual.getCpf().equals(busca)) {
                    return clienteAtual;
                }
            }
            return null;
        }

        Cliente encontrado = null;
        int contagemNome = 0;
        for (Cliente clienteAtual : listaCliente) {
            if (clienteAtual.getNome() != null && clienteAtual.getNome().equalsIgnoreCase(busca)) {
                encontrado = clienteAtual;
                contagemNome++;
            }
        }
        if (contagemNome > 1) {
            return null;
        }
        return encontrado;
    }

    public int contarClientesPorNome(String nome) {
        if (!Validar.textoNaoVazio(nome)) return 0;
        int count = 0;
        for (Cliente c : dados.getListaClientes()) {
            if (c.getNome() != null && c.getNome().equalsIgnoreCase(nome.trim())) {
                count++;
            }
        }
        return count;
    }

    public void atualizarReferenciasCpfCliente(String cpfAntigo, String cpfNovo) {
        String antigo = Validar.normalizarCpf(cpfAntigo);
        String novo = Validar.normalizarCpf(cpfNovo);
        if (antigo.isEmpty() || novo.isEmpty() || antigo.equals(novo)) {
            return;
        }
        Cliente cliente = buscaCliente(cpfNovo);
        if (cliente == null) {
            return;
        }
        for (Edificio ed : dados.getListaEdificio()) {
            for (Andar andar : ed.getAndares()) {
                for (Apartamento apt : andar.getApartamentos()) {
                    Cliente interessado = apt.getClienteInteressado();
                    if (interessado != null && Validar.normalizarCpf(interessado.getCpf()).equals(antigo)) {
                        apt.setClienteInteressado(cliente);
                    }
                }
            }
        }
    }

    public String gerarRelatorioCliente(){
        ArrayList<Cliente> listaCliente = dados.getListaClientes();
        StringBuilder sb = new StringBuilder();

        sb.append("==========================================================================================\n");
        sb.append("                                RELATÓRIO GERAL DE CLIENTES                               \n");
        sb.append("==========================================================================================\n");
        sb.append(" CPF             | NOME                           | ESTADO CIVIL    | RG                  \n");
        sb.append("-----------------|--------------------------------|-----------------|---------------------\n");

        String maskTitular = " %-15s | %-30s | %-15s | %s\n";
        String maskConjuge = "                └─ Cônjuge: %-20s | CPF: %-15s | RG: %s\n";

        for(Cliente clienteAtual : listaCliente){
            String nome = clienteAtual.getNome() == null ? "-" : clienteAtual.getNome().toUpperCase();
            String cpf = clienteAtual.getCpf() == null ? "-" : clienteAtual.getCpf();
            String rg = clienteAtual.getRg() == null ? "-" : clienteAtual.getRg();
            String estado = clienteAtual.getEstadoCivil() == null ? "-" : clienteAtual.getEstadoCivil().name();
            sb.append(String.format(maskTitular, cpf, nome, estado, rg));

            if (clienteAtual.getConjuge() != null) {
                sb.append(String.format(maskConjuge,
                        clienteAtual.getConjuge().getNome(),
                        clienteAtual.getConjuge().getCpf(),
                        clienteAtual.getConjuge().getRg()
                ));
            }
        }

        sb.append("------------------------------------------------------------------------------------------\n");
        sb.append(String.format(" Total de clientes listados: %d\n", listaCliente.size()));
        sb.append("==========================================================================================\n");

        return sb.toString();
    }

    public boolean podeEfetuarVenda(Apartamento apt, Cliente cliente) {
        return motivoVendaNegada(apt, cliente) == null;
    }

    public String motivoVendaNegada(Apartamento apt, Cliente cliente) {
        if (apt == null || cliente == null) {
            return "Dados do apartamento ou do cliente inválidos.";
        }
        if (apt.getStatus() == StatusApartamento.VENDIDO) {
            return "Este apartamento já foi vendido.";
        }
        if (apt.getStatus() != StatusApartamento.RESERVADO) {
            return "O apartamento precisa estar RESERVADO para efetuar a venda.";
        }
        Cliente reservado = apt.getClienteInteressado();
        if (reservado == null) {
            return "Reserva sem cliente vinculado. Libere o apartamento (opção 1) e reserve novamente.";
        }
        if (!Validar.normalizarCpf(reservado.getCpf()).equals(Validar.normalizarCpf(cliente.getCpf()))) {
            return "O CPF informado não corresponde ao cliente da reserva.";
        }
        return null;
    }

    public String motivoFalhaRegistroVenda(Venda venda) {
        if (venda == null || venda.getApartamento() == null) {
            return "Dados da venda inválidos.";
        }
        if (dados.existeVendaParaApartamento(venda.getIdEdificio(), venda.getApartamento().getAndar(),
                venda.getApartamento().getNumero())) {
            return "Já existe uma venda registrada para esta unidade.";
        }
        return "Falha ao salvar os dados no arquivo.";
    }

    public boolean fecharNegocio(Venda venda) {
        if (venda == null || motivoVendaNegada(venda.getApartamento(), venda.getCliente()) != null) {
            return false;
        }
        Apartamento apt = venda.getApartamento();
        StatusApartamento statusAnterior = apt.getStatus();
        Cliente interessadoAnterior = apt.getClienteInteressado();
        double sinalAnterior = apt.getValorSinal();

        apt.setStatusApartamento(StatusApartamento.VENDIDO);
        apt.setClienteInteressado(null);
        apt.setValorSinal(0);

        if (!adicionarVenda(venda)) {
            apt.setStatusApartamento(statusAnterior);
            apt.setClienteInteressado(interessadoAnterior);
            apt.setValorSinal(sinalAnterior);
            return false;
        }
        return true;
    }

    private int buscaIdInicial(){
        ArrayList<Edificio> listaEd = dados.getListaEdificio();
        int maiorID = 0;

        for(Edificio edAtual : listaEd){
            if(edAtual.getId() > maiorID) maiorID = edAtual.getId();
        }
        return maiorID;
    }

    public boolean atualizarEstagioDaObra(int idEdificio, EstagioObra estagioNovo){
        Edificio ed = buscarEdificioPorId(idEdificio);
        if (ed == null) {
            return false;
        }
        ed.setEstagioObra(estagioNovo);
        return dados.gravarArquivo();
    }

    public boolean salvarDados() {
        return dados.gravarArquivo();
    }

    public void atualizarValorApartamento(Apartamento apt, double valor){
        apt.setValorDeVenda(valor);
    }
}
