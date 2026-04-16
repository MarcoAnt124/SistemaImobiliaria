package model;

import java.util.ArrayList;

public class Andar {
    private int numero;
    private ArrayList<Apartamento> apartamentos;

    //TODO: Criar getters e setters


    public Andar(int numero) {
        this.numero = numero;
        this.apartamentos = new ArrayList<Apartamento>();
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getQuantidadeDeApartamentos() {
        return this.getApartamentos().size();
    }


    public ArrayList<Apartamento> getApartamentos() {
        return apartamentos;
    }

    public void setApartamentos(ArrayList<Apartamento> apartamentos) {
        this.apartamentos = apartamentos;
    }
}
