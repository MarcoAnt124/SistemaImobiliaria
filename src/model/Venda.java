package model;

import java.time.LocalDate;

public class Venda {
    private Vendedor vendedor;
    private Apartamento apartamento;
    private Cliente cliente;
    private LocalDate dataDaVenda;
    private double valorFinal;
    private int idEdificio;

    public Venda(Vendedor vendedor, Apartamento apartamento, Cliente cliente, int idEdificio,
                 LocalDate dataDaVenda, double valorFinal) {
        this.vendedor = vendedor;
        this.apartamento = apartamento;
        this.cliente = cliente;
        this.idEdificio = idEdificio;
        this.dataDaVenda = dataDaVenda;
        this.valorFinal = valorFinal;
    }

    public Venda(Vendedor vendedor, Apartamento apartamento, Cliente cliente, int idEdificio,
                 double percentualDesconto) {
        this.vendedor = vendedor;
        this.apartamento = apartamento;
        this.cliente = cliente;
        this.idEdificio = idEdificio;
        double precoBase = apartamento.getValorDeVenda();
        this.dataDaVenda = LocalDate.now();
        this.valorFinal = precoBase - (precoBase * (percentualDesconto / 100));
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

    public int getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(int idEdificio) {
        this.idEdificio = idEdificio;
    }
}
