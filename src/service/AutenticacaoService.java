package service;
import model.Apartamento;
import model.StatusApartamento;
import repository.DadosRepository;
import model.Vendedor;

import java.util.ArrayList;

public class AutenticacaoService {
    private DadosRepository dados;
    private Vendedor vendedorAtual;

    public AutenticacaoService(DadosRepository repositorioAtual) {
        this.dados = repositorioAtual;
        this.vendedorAtual = null;
    }

    public Vendedor getVendedorAtual() {
        return this.vendedorAtual;
    }

    public void setVendedorAtual(Vendedor vendedor) {
        this.vendedorAtual = vendedor;
    }

    public boolean verificarID(int idVendedor) {
        ArrayList<Vendedor> listVend = dados.listaVendedores();
        setVendedorAtual(null);

        for (Vendedor vendAtual : listVend) {
            if (vendAtual.getIdVendedor() == idVendedor) {
                setVendedorAtual(vendAtual);
                return true;
            }
        }
        return false;
    }

    public boolean verificarADM() {
        return vendedorAtual != null && vendedorAtual.getIdVendedor() == 666;
    }
}
