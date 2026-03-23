package model;

import java.time.LocalDate;
import

public class Venda {
    private Vendedor vendedor;
    private Apartamento apartamento;
    private Cliente cliente;
    private LocalDate dataDaVenda;
    private double valorFinal;

    public Venda(Vendedor vendedor, Apartamento apartamento, Cliente cliente, LocalDate dataDaVenda, double valorFinal) {
        this.vendedor = vendedor;
        this.apartamento = apartamento;
        this.cliente = cliente;
        this.dataDaVenda = dataDaVenda;
        this.valorFinal = valorFinal;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public void setApartamento(Apartamento apartamento) {
        this.apartamento = apartamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataDaVenda() {
        return dataDaVenda;
    }

    public void setDataDaVenda(LocalDate dataDaVenda) {
        this.dataDaVenda = dataDaVenda;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public void fecharNegocio(Vendedor vendedor, Apartamento apartamento, Cliente cliente, double percentualDesconto){
        this.vendedor = vendedor;
        this.apartamento = apartamento;
        this.cliente = cliente;

        double precoBase = apartamento.getValorDeVenda();

        this.dataDaVenda = LocalDate.now(); //defini o horario da conta

        apartamento.setStatusAparamento(VENDIDO);

        this.valorFinal = precoBase - (precoBase * (percentualDesconto / 100)); //calcular o valor de desconto
    }

    //TODO: Criar getters, setters, construtor e o método fecharNegocio()
    //OBS: Lembre-se que no método "fecharNegocio()" um desconto (entrada do usuário) deve ser
    // aplicado ao "valorFinal"


}
