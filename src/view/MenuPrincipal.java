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
        while (true) {
            limparConsole();
            System.out.println("========================================");
            System.out.println("          SISTEMA IMOBILIÁRIO");
            System.out.println("========================================");
            System.out.println(" 0 - Encerrar o sistema");
            System.out.print("Digite seu ID de acesso: ");

            int id = lerInt();
            if (id == 0) {
                System.out.println("Sistema encerrado. Até logo!");
                break;
            }

            if (serviceAut.verificarID(id)) {
                if (serviceAut.verificarADM()) {
                    menuAdministrativo();
                }
                exibirMenuPrincipal();
                serviceAut.setVendedorAtual(null);
            } else {
                System.out.println("\n[ERRO] O ID " + id + " não está cadastrado!");
                pausar();
            }
        }
    }

    private int lerInt() {
        while (true) {
            try {
                if (!scan.hasNextInt()) {
                    System.out.println("[ERRO] Digite um número válido.");
                    scan.next();
                    continue;
                }
                int valor = scan.nextInt();
                scan.nextLine();
                return valor;
            } catch (java.util.NoSuchElementException e) {
                System.out.println("[ERRO] Entrada encerrada.");
                return 0;
            }
        }
    }

    private void menuAdministrativo() {
        int config;
        do {
            limparConsole();
            System.out.println("==== PAINEL ADMINISTRATIVO (ID 666) ====");
            System.out.println("1 - Cadastrar Novo Vendedor");
            System.out.println("0 - Prosseguir para o Sistema");
            System.out.print("Escolha: ");
            config = lerInt();

            if (config == 1) {
                System.out.print("Nome do novo Vendedor: ");
                String nome = scan.nextLine();
                System.out.print("ID para o novo Vendedor: ");
                int idNovo = lerInt();

                if (serviceImo.adicionarVendedor(nome, idNovo)) {
                    System.out.println("[OK] Vendedor cadastrado com sucesso!");
                } else {
                    System.out.println("[ERRO] Falha ao cadastrar vendedor (ID já existe ou erro ao salvar).");
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
            String nomeVendedor = (logado != null && logado.getNome() != null) ? logado.getNome() : "VENDEDOR";
            System.out.println("   SEJA BEM-VINDO, " + nomeVendedor.toUpperCase());
            System.out.println("========================================");
            System.out.println(" 1 - Gestão de Imóveis");
            System.out.println(" 2 - Gestão de Clientes");
            System.out.println(" 3 - Efetuar Venda (Fechar Negócio)");
            System.out.println(" 4 - Histórico de Vendas");
            System.out.println(" 0 - Sair da sessão (voltar ao login)");
            System.out.println("----------------------------------------");
            System.out.print("Escolha uma opção: ");

            op = lerInt();

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