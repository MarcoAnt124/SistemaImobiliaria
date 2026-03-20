package model;

import java.time.LocalDate;

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

    public void fecharNegocio(double percentualDesconto){
        if(percentualDesconto <= 10 && percentualDesconto >= 0){
            this.valorFinal = this.valorFinal - (this.valorFinal * (percentualDesconto / 100));
        }else{
            System.out.println("Desconto inválido! O valor deve ser entre 0% e 10%");
        }
    }

    //TODO: Criar getters, setters, construtor e o método fecharNegocio()
    //OBS: Lembre-se que no método "fecharNegocio()" um desconto (entrada do usuário) deve ser
    // aplicado ao "valorFinal", sendo no máximo de 10%


}
