package view;

import model.Vendedor;
import service.AutenticacaoService;
import service.ImobiliariaService;

import java.util.Scanner;

public class MenuPrincipal {
    private AutenticacaoService serviceAut;
    private ImobiliariaService serviceImo;
    private SubMenus subMenus;

    public MenuPrincipal(AutenticacaoService serviceAut, ImobiliariaService serviceImo, SubMenus subMenus) {
        this.serviceAut = serviceAut;
        this.serviceImo = serviceImo;
        this.subMenus = subMenus;
    }

    public void menu() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("LOGIN");
        System.out.println("Digite seu ID: ");
        int id = scanner.nextInt();

        if (serviceAut.verificarID(id)) {
            if (serviceAut.verificarADM()) {
                int config = scanner.nextInt();
                scanner.nextLine();
                do {
                    System.out.println("1 - Criar novo usuario");
                    System.out.println("0 - Sair");
                    if(config == 1){
                        System.out.println("Insira o nome do Vendedor:");
                        String nome = scanner.nextLine();

                        System.out.println("Insira o ID do Vendedor:");
                        int idNovo = scanner.nextInt();

                        if ((serviceImo.adicionarVendedor(nome, idNovo))) {
                            System.out.println("Cadastro concluido!!!");
                        } else {
                            System.out.println("Erro no Cadastro!!!");
                        }
                    }

                } while (config != 0);
            }
        } else {
            System.out.println("O ID" + id + " não está cadastrado!!!!");
            return;
        }

        if (serviceAut.getVendedorAtual() == null) {
            System.out.println("Erro ao fazer login!");
            return;
        }

        System.out.println("SEJA BEM-VINDO!!!," + " vendedor.getnome()");

        int opcaomenuprincipal;

        do {
            System.out.println("\n  MENU PRINCIPAL");
            System.out.println("1 - Gestão de Imoveis");
            System.out.println("2 - Gestão de Clientes");
            System.out.println("3 - Realizar Venda");
            System.out.println("4 - Histórico de Vendas");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcaomenuprincipal = scanner.nextInt();

            switch (opcaomenuprincipal) {
                case 1:
                    subMenus.menuImoveis();
                    break;
                case 2:
                    subMenus.menuClientes();
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("Exibindo vendas realizadas");
                    break;
                case 0:
                    System.out.println("Saindo");
                    break;
                default:
                    System.out.println("Opcao Invalida!");
            }
        } while (opcaomenuprincipal != 0);
    }

    public void menuImoveis(Scanner scanner) {
        int opcaomenuimoveis;

        do {
            System.out.println("\n Gestão de Imoveis");
            System.out.println("1 - Cadastrar edificio");
            System.out.println("2 - Alterar situação do apartamento");
            System.out.println("3 - Listar disponiveis");
            System.out.println("4 - Listar reservados");
            System.out.println("5 - Listar vendidos");
            System.out.println("6 - Sair");
            System.out.print("Escolha: ");

            opcaomenuimoveis = scanner.nextInt();

            switch (opcaomenuimoveis) {
                case 1:
                    System.out.println("Cadastrando edificio");
                    break;
                case 2:
                    System.out.println("Alterando status");
                    break;
                case 3:
                    System.out.println("Listando disponiveis");
                    break;
                case 4:
                    System.out.println("Listando reservados");
                    break;
                case 5:
                    System.out.println("Listando vendidos");
                    break;
                case 6:
                    System.out.println("Voltando");
                    break;
                default:
                    System.out.println("Opcao invalida!");
            }
        } while (opcaomenuimoveis != 6);
    }

    public void menuClientes(Scanner scanner) {
        int opcaomenuclientes;

        do {
            System.out.println("\n Gestao de cliente");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Buscar por CPF");
            System.out.println("3 - Buscar por nome");
            System.out.println("4 - Sair");
            System.out.print("Escolha: ");

            opcaomenuclientes = scanner.nextInt();

            switch (opcaomenuclientes) {
                case 1:
                    System.out.println("Cadastrando cliente");
                    break;
                case 2:
                    System.out.println("Buscando por CPF");
                    break;
                case 3:
                    System.out.println("Buscando por nome");
                    break;
                case 4:
                    System.out.println("Voltando");
                    break;
                default:
                    System.out.println("Opcao invalida!");

            }
        } while (opcaomenuclientes != 4) ;
    }
}
