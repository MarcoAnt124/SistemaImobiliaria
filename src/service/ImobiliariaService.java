package service;
import model.Andar;
import model.Apartamento;
import model.Edificio;
import model.StatusApartamento;
import repository.DadosRepository;

import java.util.ArrayList;

public class ImobiliariaService {
    private DadosRepository dados;

    //TODO: criar método de instancia do edificio
    //TODO: criar método de processamento de venda
    //TODO: criar um método de busca de informações


    public ImobiliariaService(DadosRepository dados) {
        this.dados = dados;
    }

    public boolean verificarStatusDisponivel(Apartamento apt){
        return apt.getStatus() == StatusApartamento.DISPONIVEL;
    }

    public Edificio gerarEdificio(int qtdAndares, int qtdApartamentos, Edificio projetoAtual){
        for(int i = 0; i < qtdAndares; i++){

            for(int j = 0; j < qtdApartamentos; j++){
            }
        }
        return null;
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
}
