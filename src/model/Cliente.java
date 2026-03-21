package model;

public class Cliente {
    private String nome;
    private String cpf;
    private String rg;
    private String estadoCivil;
    private Cliente conjuge;

    public Cliente(String nome, String cpf, String rg, String estadoCivil) { //construtor de cliente sem conjuge
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.estadoCivil = estadoCivil;
        this.conjuge = null;
    }

    public Cliente(String nome, String cpf, String rg, String estadoCivil, Cliente conjuge) { //construtor de cliente com conjuge
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

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        if (estadoCivil.equalsIgnoreCase("Casado") && this.conjuge == null) { //se tentar colocar como casado mas nao possuir conjuge
            System.out.println("Erro: Para alterar o estado para Casado, é necessário informar o cônjuge primeiro");
        } else {
            this.estadoCivil = estadoCivil;
        }
    }

    public Cliente getConjuge() {
        return conjuge;
    }

    public void setConjuge(Cliente conjuge) {
        this.conjuge = conjuge;
    }
}
