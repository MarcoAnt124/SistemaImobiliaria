package view;

import model.Andar;
import model.Apartamento;
import model.Edificio;
import model.StatusApartamento;
import repository.DadosRepository;
import service.ImobiliariaService;

import java.util.ArrayList;
import java.util.Scanner;

public class SubMenus {
    ImobiliariaService service;
    public void menuImoveis(){
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
                    service.gerarRelatorioTotal(service.getDados().getListaEdificio());
                    break;

                case 2:
                    cadastrarEdificio();
                    break;

                case 3:
                    consultarApartamento();
                    break;

                case 4:
                    alterarDadosApartamento();
                    break;

                default:
                    System.out.println("Opção Inválida!!!");
                    break;
            }
        } while(op != 0);
    }

    private void cadastrarEdificio() {
        Scanner scan = new Scanner(System.in);

        System.out.println("\n--- CADASTRO DE EDIFÍCIO ---");
        System.out.print("ID: ");
        int id = scan.nextInt();
        scan.nextLine();

        System.out.print("Nome: ");
        String nome = scan.nextLine();

        System.out.print("Endereço: ");
        String endereco = scan.nextLine();

        // Service inicia a criação
        Edificio edificio = service.iniciarNovoEdificio(id, nome, endereco);

        System.out.print("Total de Andares: ");
        int totalAndares = scan.nextInt();

        // Coleta manual dos dados de cada unidade
        for (int i = 1; i <= totalAndares; i++) {
            System.out.print("Apartamentos por Andar: ");
            int totalAptos = scan.nextInt();
            for (int j = 1; j <= totalAptos; j++) {
                int numeroApto = (i * 100) + j;
                System.out.println("\nConfigurando Unidade " + numeroApto + ":");

                System.out.print("   Área (m²): ");
                double area = scan.nextDouble();
                System.out.print("   Preço (R$): ");
                double preco = scan.nextDouble();
                System.out.print("   Quantidade de Quartos: ");
                int qtdQuartos = scan.nextInt();
                System.out.print("   Quantidade de Banheiros: ");
                int qtdBanheiros = scan.nextInt();

                // Service processa a montagem do objeto
                service.vincularApartamento(edificio, i, numeroApto, area, preco, totalAptos, qtdQuartos, qtdBanheiros);
            }
        }

        // Service finaliza salvando no repositório
        service.salvarEdificio(edificio);
        System.out.println("\n[SUCESSO] Edifício cadastrado.");
    }

    public void consultarApartamento() {
        Scanner scan = new Scanner(System.in);

        //Listagem básica de edifícios (ID, Nome, Endereço)
        System.out.println("\n======================================================================");
        System.out.println("                      LISTA DE EDIFÍCIOS");
        System.out.println("======================================================================");

        // Aqui a View chama o seu Service, que por sua vez busca no Repository
        System.out.println(service.gerarListaSimplesEdificios());

        System.out.print("Digite o ID do Edifício que deseja consultar: ");
        int idBusca = scan.nextInt();

        //Busca o objeto Edifício pelo ID
        Edificio ed = service.buscarEdificioPorId(idBusca);

        if (ed == null) {
            System.out.println("[ERRO] Edifício com ID " + idBusca + " não encontrado.");
            return;
        }

        //Listagem básica dos andares do prédio escolhido
        System.out.println("\n--- ANDARES DISPONÍVEIS NO " + ed.getNome().toUpperCase() + " ---");
        for (Andar andar : ed.getAndares()) {
            System.out.printf("Andar: %02dº | Quantidade de Apartamentos: %d\n",
                    andar.getNumero(), andar.getApartamentos().size());
        }

        System.out.print("\nDigite o número do andar que deseja ver os detalhes: ");
        int numAndarBusca = scan.nextInt();

        //Busca o objeto Andar dentro do Edifício
        Andar andarEscolhido = service.buscarAndarNoEdificio(ed, numAndarBusca);

        if (andarEscolhido == null) {
            System.out.println("[ERRO] Andar " + numAndarBusca + " não encontrado neste edifício.");
            return;
        }

        //Tabela detalhada dos apartamentos do andar (A parte chata do String.format)
        System.out.println("\n===========================================================================================");
        System.out.println("           DETALHES DOS APARTAMENTOS - ANDAR " + numAndarBusca + "º");
        System.out.println("===========================================================================================");
        System.out.println(" APTO | ÁREA (m²) | VALOR (R$)    | QUARTOS | BANHEIROS | STATUS");
        System.out.println("------|-----------|---------------|---------|-----------|----------------------------------");

        for (Apartamento apt : andarEscolhido.getApartamentos()) {
            System.out.printf(new java.util.Locale("pt", "BR"),
                    " %-4d | %-9.1f | R$ %,-12.2f | %-7d | %-9d | %s\n",
                    apt.getNumero(),
                    apt.getMetragem(),
                    apt.getValorDeVenda(),
                    apt.getQuantidadeDeQuartos(),
                    apt.getQuantidadeDeBanheiros(),
                    apt.getStatus().toString().toUpperCase()
            );
        }
        System.out.println("===========================================================================================\n");
    }

    private void alterarDadosApartamento(){
        Scanner scan = new Scanner(System.in);

        //Selecionar Edifício
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.print("ID do Edifício: ");
        int idEd = scan.nextInt();
        Edificio ed = service.buscarEdificioPorId(idEd);

        if (ed == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return;
        }

        //Selecionar Andar
        System.out.println("\n--- ANDARES DO " + ed.getNome().toUpperCase() + " ---");
        for (Andar a : ed.getAndares()) {
            System.out.printf("Andar: %02dº | Unidades: %d\n", a.getNumero(), a.getApartamentos().size());
        }

        System.out.print("Número do Andar: ");
        int numAndar = scan.nextInt();
        Andar andar = service.buscarAndarNoEdificio(ed, numAndar);

        if (andar == null) {
            System.out.println("[ERRO] Andar não encontrado.");
            return;
        }

        //Selecionar Apartamento
        System.out.println("\n--- UNIDADES NO " + numAndar + "º ANDAR ---");
        for (Apartamento a : andar.getApartamentos()) {
            System.out.print("[" + a.getNumero() + "] ");
        }
        System.out.print("\nNúmero do Apartamento para editar: ");
        int numApto = scan.nextInt();

        Apartamento apto = service.buscarApartamentoNoAndar(andar, numApto);

        if (apto == null) {
            System.out.println("[ERRO] Apartamento não encontrado.");
            return;
        }

        menuAlteracao(apto, scan);
    }

    public void menuAlteracao(Apartamento apt, Scanner scan){
        int subOp;
        do {
            System.out.println("\n====================================================");
            System.out.println("       EDITANDO APARTAMENTO " + apt.getNumero());
            System.out.println("====================================================");
            System.out.printf(" 1. Status        | Atual: %s\n", apt.getStatus());
            System.out.printf(" 2. Valor Venda   | Atual: R$ %,.2f\n", apt.getValorDeVenda());
            System.out.printf(" 3. Valor Sinal   | Atual: R$ %,.2f\n", apt.getValorSinal());
            System.out.println(" 0. Concluir/Voltar");
            System.out.println("----------------------------------------------------");
            System.out.print("Escolha o que alterar: ");
            subOp = scan.nextInt();

            switch (subOp) {
                case 1:
                    System.out.print("Novo Status (1-DISPONIVEL, 2-RESERVADO): ");
                    int st = scan.nextInt();
                    if(!service.atualizarStatus(apt, st)){
                        System.out.println("Opção Inválida!!!");
                    };
                    break;
                case 2:
                    System.out.print("Novo Valor de Venda: R$ ");
                    double novoValor = scan.nextDouble();
                    apt.setValorDeVenda(novoValor);
                    break;
                case 3:
                    System.out.print("Novo Valor de Sinal: R$ ");
                    double novoSinal = scan.nextDouble();
                    apt.setValorSinal(novoSinal);
                    break;
                case 0:
                    System.out.println("[OK] Alterações finalizadas.");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (subOp != 0);
    }
}
