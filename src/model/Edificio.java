package model;

import java.util.ArrayList;

public class Edificio {
    private int id;
    private String nome;
    private String endereco;
    private ArrayList<Andar> andares;

    //TODO: Criar getters, setters

    public Edificio(int id, String nome, String endereco) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.andares = new ArrayList<Andar>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public ArrayList<Andar> getAndares() {
        return andares;
    }

    public void setAndares(ArrayList<Andar> andares) {
        this.andares = andares;
    }
}


