package model;

public class Cliente {
    private String nome;
    private String cpf;
    private String rg;
    private EstadoCivil estadoCivil;
    private Conjuge conjuge;

    public Cliente(String nome, String cpf, String rg, EstadoCivil estadoCivil) { //construtor de cliente sem conjuge
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.estadoCivil = estadoCivil;
        this.conjuge = null;
    }

    public Cliente() {
    }

    public Cliente(String nome, String cpf, String rg, EstadoCivil estadoCivil, Conjuge conjuge) { //construtor de cliente com conjuge
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.estadoCivil = estadoCivil;
        this.conjuge = conjuge;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Conjuge getConjuge() {return conjuge;
    }

    public void setConjugue(Conjuge conjuge) {
        this.conjuge = conjuge;
    }


}
