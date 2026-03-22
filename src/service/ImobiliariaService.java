package service;
import model.Andar;
import model.Apartamento;
import model.Edificio;
import model.StatusApartamento;
import repository.DadosRepository;

public class ImobiliariaService {
    private DadosRepository dados;

    //TODO: criar método de instancia do edificio
    //TODO: criar método de processamento de venda
    //TODO: criar um método de busca de informações


    public ImobiliariaService(DadosRepository dados) {
        this.dados = dados;
    }

    public boolean verificarStatusDisponivel(Apartamento apt){
        return apt.getStatus() == StatusApartamento.DISPONIVEL;
    }

    public void gerarEdificio(int qtdAndares, int qtdApartamentos){
        Edificio novoEdificio = new Edificio();
        for(int i = 0; i < qtdAndares; i++){
            novoEdificio.getAndares().add(new Andar((i*100), qtdApartamentos));
            for(int j = 0; j < qtdApartamentos; j++){
                novoEdificio.getAndares().getApartamentos.add(new Apartamento())
            }
        }
    }
}
