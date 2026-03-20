package model;

public class Vendedor {
    private int idVendedor;
    private String nome;
    private String senha;

    //TODO: Criar getters, setters e construtor


    public Vendedor(int idVendedor, String nome, String senha) {
        this.idVendedor = idVendedor;
        this.nome = nome;
        this.senha = senha;
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

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }
}
