package model;

public class Vendedor {
    private int idVendedor;
    private String nome;;

    //TODO: Criar getters, setters e construtor


    public Vendedor(int idVendedor, String nome) {
        this.idVendedor = idVendedor;
        this.nome = nome;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getNome() {
        return nome;
    }
}
