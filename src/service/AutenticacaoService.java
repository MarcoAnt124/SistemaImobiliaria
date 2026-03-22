package service;
import repository.DadosRepository;
import model.Vendedor;

import java.util.ArrayList;

public class AutenticacaoService {
    private DadosRepository dados;
    private Vendedor vendedorAtual;

    public AutenticacaoService() {
        this.dados = null;
        this.vendedorAtual = null;
    }

    public Vendedor verificarID(int idVendedor) {
        ArrayList<Vendedor> listVend = listVend = dados.listaVendedores();

        for (Vendedor vendAtual : listVend) {
            if (vendAtual.getIdVendedor() == idVendedor) return vendAtual;
        }
        return null;
    }

    public boolean verificarADM(Vendedor vendedor){
        return vendedor.getIdVendedor() == 666;
    }
}
