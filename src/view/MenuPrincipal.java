package view;

import model.Vendedor;
import service.AutenticacaoService;
import service.ImobiliariaService;

import java.util.Scanner;

public class MenuPrincipal {
    private AutenticacaoService serviceAut;
    private ImobiliariaService serviceImo;
    private SubMenus subMenus;
    private Scanner scan;

    public MenuPrincipal(AutenticacaoService serviceAut, ImobiliariaService serviceImo, SubMenus subMenus, Scanner scan) {
        this.serviceAut = serviceAut;
        this.serviceImo = serviceImo;
        this.subMenus = subMenus;
        this.scan = scan;
    }

    public void iniciarSistema() {
        limparConsole();
        int id;
        do {
            System.out.println("========================================");
            System.out.println("          SISTEMA IMOBILIÁRIO");
            System.out.println("========================================");
            System.out.print("Digite seu ID de acesso: ");
            id = scan.nextInt();
            scan.nextLine();

            if (serviceAut.verificarID(id)) {
                if (serviceAut.verificarADM()) {
                    menuAdministrativo();
                }

                exibirMenuPrincipal();

            } else {
                System.out.println("\n[ERRO] O ID " + id + " não está cadastrado!");
                pausar();
            }
        }while(!serviceAut.verificarID(id));
    }

    private void menuAdministrativo() {
        int config;
        do {
            limparConsole();
            System.out.println("==== PAINEL ADMINISTRATIVO (ID 666) ====");
            System.out.println("1 - Cadastrar Novo Vendedor");
            System.out.println("0 - Prosseguir para o Sistema");
            System.out.print("Escolha: ");
            config = scan.nextInt();
            scan.nextLine();

            if (config == 1) {
                System.out.print("Nome do novo Vendedor: ");
                String nome = scan.nextLine();
                System.out.print("ID para o novo Vendedor: ");
                int idNovo = scan.nextInt();
                scan.nextLine();

                if (serviceImo.adicionarVendedor(nome, idNovo)) {
                    System.out.println("[OK] Vendedor cadastrado com sucesso!");
                } else {
                    System.out.println("[ERRO] Falha ao cadastrar vendedor.");
                }
                pausar();
            }
        } while (config != 0);
    }

    private void exibirMenuPrincipal() {
        Vendedor logado = serviceAut.getVendedorAtual();
        int op;

        do {
            limparConsole();
            System.out.println("========================================");
            System.out.println("   SEJA BEM-VINDO, " + logado.getNome().toUpperCase());
            System.out.println("========================================");
            System.out.println(" 1 - Gestão de Imóveis");
            System.out.println(" 2 - Gestão de Clientes");
            System.out.println(" 3 - Efetuar Venda (Fechar Negócio)");
            System.out.println(" 4 - Histórico de Vendas");
            System.out.println(" 0 - Sair do Sistema");
            System.out.println("----------------------------------------");
            System.out.print("Escolha uma opção: ");

            op = scan.nextInt();
            scan.nextLine();

            switch (op) {
                case 1:
                    subMenus.menuImoveis();
                    break;
                case 2:
                    subMenus.menuClientes();
                    break;
                case 3:
                    subMenus.fecharVenda();
                    break;
                case 4:
                    subMenus.listarVendas();
                    pausar();
                    break;
                case 0:
                    System.out.println("Finalizando sessão... Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    pausar();
            }
        } while (op != 0);
    }

    private void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pausar() {
        System.out.println("\nPressione ENTER para continuar...");
        scan.nextLine();
    }
}