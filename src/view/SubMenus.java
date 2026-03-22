package view;

import repository.DadosRepository;
import service.ImobiliariaService;

import java.util.Scanner;

public class SubMenus {
    ImobiliariaService service;
    DadosRepository dados;
    public void gestaoDeImoveis(){
        Scanner scan = new Scanner(System.in);
        int op;
        do{
            System.out.println("\n====Gestão de Imoveis====");
            System.out.println("1 - Ver Disponibilidade dos Apartamentos");
            System.out.println("2 - Cadastrar novo Edificio");
            System.out.println("3 - Consultar Apartamento Especifico");
            System.out.println("4 - Alterar dados de Apartamento");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            op = scan.nextInt();
            scan.nextLine();

            switch(op){
                case 1:
                    service.gerarRelatorioTotal(dados.listaEdificios);
            }
        } while(op != 0);
    }
}
