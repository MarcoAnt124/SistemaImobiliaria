package service;
import model.*;
import repository.DadosRepository;

import java.util.ArrayList;

public class ImobiliariaService {
    private DadosRepository dados;

    //TODO: criar método de processamento de venda
    //TODO: criar um método de busca de informações


    public ImobiliariaService(DadosRepository dados) {
        this.dados = dados;
    }

    public boolean verificarStatusDisponivel(Apartamento apt){
        return apt.getStatus() == StatusApartamento.DISPONIVEL;
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

    public void vincularApartamento(Edificio edificio, int numAndar,int numApt, double area, double preco, int qtdApts, int qtdQuartos, int qtdBanheiros){
        Andar novoAndar = null;
        for(Andar andarAtual : edificio.getAndares()){
            if(andarAtual.getNumero() == numAndar){
                novoAndar = andarAtual;
                break;
            }
        }

        if(novoAndar == null){
            novoAndar = new Andar(numAndar, qtdApts);
            edificio.getAndares().add(novoAndar);
        }

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

    public boolean atualizarStatus(Apartamento apt, int op){
        if(op != 1 && op != 2) return false;

        if ((op == 1)) {
            apt.setStatus(StatusApartamento.DISPONIVEL);
        } else {
            apt.setStatus(StatusApartamento.RESERVADO);
        }

        return true;
    }

    public boolean adicionarVendedor(String nome, int id){
        Vendedor novoVendedor = new Vendedor(id, nome);
        return (dados.anexarVendedor(novoVendedor));
    }
}
