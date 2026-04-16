package service;
import model.*;
import repository.DadosRepository;

import java.util.ArrayList;

public class ImobiliariaService {
    private DadosRepository dados;
    private AutenticacaoService autenticacaoService;
    private int IDgenerator;

    //TODO: criar método de processamento de venda
    //TODO: criar um método de busca de informações

    public ImobiliariaService(DadosRepository dados, AutenticacaoService autenticacaoService) {
        this.dados = dados;
        this.autenticacaoService = autenticacaoService;
        this.IDgenerator = buscaIdInicial();
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

        for (Venda vendaAtual : listaVendas) {
            String dataFormatada = vendaAtual.getDataDaVenda().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String nomeCli = vendaAtual.getCliente().getNome();
            if(nomeCli.length() > 20) nomeCli = nomeCli.substring(0, 17) + "...";

            sb.append(String.format(new java.util.Locale("pt", "BR"), mask,
                    dataFormatada,
                    vendaAtual.getVendedor().getNome(),
                    vendaAtual.getApartamento().getNumero(),
                    nomeCli,
                    vendaAtual.getValorFinal()
            ));

            somaTotal += vendaAtual.getValorFinal();
        }

        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format(new java.util.Locale("pt", "BR"),
                " TOTAL DE VENDAS: %-5d | VOLUME FINANCEIRO TOTAL: R$ %,-20.2f\n",
                listaVendas.size(), somaTotal));
        sb.append("====================================================================================================\n");

        return sb.toString();
    }

    public DadosRepository getDados(){
        return this.dados;
    }

    public String gerarRelatorioTotal(ArrayList<Edificio> listaEdificio){
        StringBuilder builder = new StringBuilder();
        builder.append("======================================================================\n");
        builder.append(String.format("%70s\n", "LISTA DE EDIFICIOS DISPONÍVEIS\n"));
        builder.append("======================================================================\n");
            for(Edificio edificioAtual : listaEdificio){
                builder.append("\nEdifício: ").append(edificioAtual.getNome().toUpperCase());
                builder.append("\nEndereço: ").append(edificioAtual.getEndereco()).append("\n");

                builder.append(gerarRelatorioBase(edificioAtual));

                builder.append("\n");
            }
        return builder.toString();
    }

    public String gerarRelatorioBase(Edificio edificio) {
        StringBuilder sb = new StringBuilder();
        int contador = 0;

        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append(String.format("%45s\n", edificio.getNome().toUpperCase()));
        sb.append("=".repeat(70)).append("\n");

        sb.append("  NÚMERO  |  ANDAR  |  ÁREA (m²)  |  VALOR (R$)    |  STATUS\n");
        sb.append("-".repeat(70)).append("\n");

        for (Andar andarAtual : edificio.getAndares()) {
            for (Apartamento aptAtual : andarAtual.getApartamentos()) {
                String status = aptAtual.getStatus().toString();

                if (status.equalsIgnoreCase("Disponível") || status.equalsIgnoreCase("Reservado")) {
                    contador++;

                    sb.append(String.format(new java.util.Locale("pt", "BR"),
                            "  %-8d |  %-6s |  %-10.1f | R$ %,-12.2f | %s\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero() + "º",
                            aptAtual.getMetragem(),
                            aptAtual.getValorDeVenda(),
                            status.toUpperCase()
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
        Edificio novoEdificio = new Edificio(id, nome, endereco);
        return novoEdificio;
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
    public void vincularApartamento(Edificio edificio, int numAndar,double area, double preco, int qtdQuartos, int qtdBanheiros){
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
    }

    public void salvarEdificio(Edificio edificio){
        dados.anexarEdificio(edificio);
    }

    public String gerarListaSimplesEdificios(){
        ArrayList<Edificio> Edificios = dados.getListaEdificio();
        StringBuilder sb = new StringBuilder();

        sb.append("======================================================================\n");
        sb.append(String.format(" %-4s | %-25s | %s\n", "ID", "NOME", "ENDEREÇO"));
        sb.append("----------------------------------------------------------------------\n");

        for(Edificio edificioAtual : Edificios){
            sb.append(String.format(" %03d  | %-25s | %s\n",
                    edificioAtual.getId(),
                    edificioAtual.getNome(),
                    edificioAtual.getEndereco()));
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

    public boolean atualizarStatus(Apartamento apt, int op, String cpf){
        if(op != 1 && op != 2) return false;

        if ((op == 1)) {
            apt.setStatus(StatusApartamento.DISPONIVEL);
            apt.setCpfInteressado("");
            apt.setValorSinal(0);
        } else {
            apt.setStatus(StatusApartamento.RESERVADO);
            apt.setCpfInteressado(cpf);
        }

        return true;
    }

    public boolean adicionarVendedor(String nome, int id){
        Vendedor novoVendedor = new Vendedor(id, nome);
        return (dados.anexarVendedor(novoVendedor));
    }

    public boolean adicionarCliente(Cliente cliente){
        return (dados.anexarCliente(cliente));
    }

    private boolean adicionarVenda(Venda venda){
        return(dados.anexarVenda(venda));
    }

    public Cliente buscaCliente(String dadoCliente){
        ArrayList<Cliente> listaCliente = dados.getListaClientes();

        for(Cliente clienteAtual : listaCliente){
            if(clienteAtual.getNome().equals(dadoCliente) || clienteAtual.getCpf().equals(dadoCliente)){
                return clienteAtual;
            }
        }

        return null;
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
            sb.append(String.format(maskTitular,
                    clienteAtual.getCpf(),
                    clienteAtual.getNome().toUpperCase(),
                    clienteAtual.getEstadoCivil(),
                    clienteAtual.getRg()
            ));

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

    public boolean fecharNegocio(Venda venda){;
        return adicionarVenda(venda);
    }

    private int buscaIdInicial(){
        ArrayList<Edificio> listaEd = dados.getListaEdificio();
        int maiorID = 0;

        for(Edificio edAtual : listaEd){
            if(edAtual.getId() > maiorID) maiorID = edAtual.getId();
        }
        return maiorID;
    }
}
