package model;

public class Apartamento {
    private StatusApartamento status;
    private int numero;
    private int andar;
    private double metragem;
    private int quantidadeDeQuartos;
    private int quantidadeDeBanheiros;
    private double valorDeVenda;
    private double valorSinal;
    private Cliente clienteInterassado;

    public void setStatus(StatusApartamento statusNovo){
        this.status = statusNovo;
    }

    public StatusApartamento getStatus(){
        return this.status;
    }

    public Apartamento(int numero, int andar, double metragem, int quantidadeDeQuartos, int quantidadeDeBanheiros, double valorDeVenda) {
        this.status = StatusApartamento.DISPONIVEL;
        this.numero = numero;
        this.andar = andar;
        this.metragem = metragem;
        this.quantidadeDeQuartos = quantidadeDeQuartos;
        this.quantidadeDeBanheiros = quantidadeDeBanheiros;
        this.valorDeVenda = valorDeVenda;
        this.valorSinal = 0;
        this.clienteInterassado = null;
    }

    public Cliente getClienteInteressado() {
        return clienteInterassado;
    }

    public void setClienteInteressado(Cliente clienteInteressado) {
        this.clienteInterassado = clienteInteressado;
    }

    public void setStatusApartamento(StatusApartamento status){
        this.status = status;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getAndar() {
        return andar;
    }

    public void setAndar(int andar) {
        this.andar = andar;
    }

    public double getMetragem() {
        return metragem;
    }

    public void setMetragem(double metragem) {
        this.metragem = metragem;
    }

    public int getQuantidadeDeQuartos() {
        return quantidadeDeQuartos;
    }

    public void setQuantidadeDeQuartos(int quantidadeDeQuartos) {
        this.quantidadeDeQuartos = quantidadeDeQuartos;
    }

    public int getQuantidadeDeBanheiros() {
        return quantidadeDeBanheiros;
    }

    public void setQuantidadeDeBanheiros(int quantidadeDeBanheiros) {
        this.quantidadeDeBanheiros = quantidadeDeBanheiros;
    }

    public double getValorDeVenda() {
        return valorDeVenda;
    }

    public void setValorDeVenda(double valorDeVenda) {
        this.valorDeVenda = valorDeVenda;
    }

    public double getValorSinal() {
        return valorSinal;
    }

    public void setValorSinal(double valorSinal) {
        this.valorSinal = valorSinal;
    }
}
