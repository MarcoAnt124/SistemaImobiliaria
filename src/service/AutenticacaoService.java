package service;
import repository.DadosRepository;
import model.Vendedor;

import java.util.ArrayList;

public class AutenticacaoService {
    private DadosRepository dados;

    public AutenticacaoService(DadosRepository dados) {
        this.dados = dados;
    }

    public Vendedor verificarUsuario(String nome, String senha){

        ArrayList<Vendedor> listaTemp = dados.listaVendedores();

        if(listaTemp == null ||  listaTemp.isEmpty()) return null;

        Vendedor temp = verificarNome(nome, listaTemp);

        return (temp != null && verificarSenha(senha, temp)) ? temp : null;

    }

    private Vendedor verificarNome(String nome, ArrayList<Vendedor> lista){
        for (Vendedor vendedorAtual : lista){
            if(vendedorAtual.getNome().equals(nome)) return vendedorAtual;
        }

        return null;
    }

    private boolean verificarSenha(String senha, Vendedor vendedor){
        return vendedor.getSenha().equals(senha);
    }
}
