package service;
import model.Apartamento;
import model.StatusApartamento;
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

    public void setVendedorAtual(Vendedor vendedor){
        this.vendedorAtual = vendedor;
    }

    public Vendedor verificarID(int idVendedor) {
        ArrayList<Vendedor> listVend = listVend = dados.listaVendedores();

        for (Vendedor vendAtual : listVend) {
            if (vendAtual.getIdVendedor() == idVendedor) ;
        }
        return null;
    }

    public boolean verificarADM(Vendedor vendedor){
        if(vendedor == null) return false;
        return vendedor.getIdVendedor() == 666;
    }

    public boolean verificarStatusDisponivel(Apartamento apt){
        return apt.getStatus() == StatusApartamento.DISPONIVEL;
    }


}
