package model;

import java.util.ArrayList;

public class Andar {
    private int numero;
    private int quantidadeDeApartamentos;
    private ArrayList<Apartamento> apartamentos;

    //TODO: Criar getters e setters


    public Andar(int numero, int quantidadeDeApartamentos, ArrayList<Apartamento> apartamentos) {
        this.numero = numero;
        this.quantidadeDeApartamentos = quantidadeDeApartamentos;
        this.apartamentos = apartamentos;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getQuantidadeDeApartamentos() {
        return quantidadeDeApartamentos;
    }

    public void setQuantidadeDeApartamentos(int quantidadeDeApartamentos) {
        this.quantidadeDeApartamentos = quantidadeDeApartamentos;
    }

    public ArrayList<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public void setApartamentos(ArrayList<Apartamento> apartamentos) {
        this.apartamentos = apartamentos;
    }
}
