package view;

import model.*;
import service.AutenticacaoService;
import service.ImobiliariaService;
import validation.Validar;

import java.util.Scanner;

public class SubMenus {
    private ImobiliariaService service;
    private Validar validar;
    private Scanner scan;
    private AutenticacaoService autenticacaoService;

    public SubMenus(ImobiliariaService service, Validar validar, Scanner scan, AutenticacaoService autenticacaoService) {
        this.service = service;
        this.validar = validar;
        this.scan = scan;
        this.autenticacaoService = autenticacaoService;
    }

    public void listarVendas(){
        System.out.println(service.gerarListaVendas());
    }

    public void menuImoveis(){
        int op;
        do{
            limparConsole();
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
                    menuDisponibilidade();
                    break;

                case 2:
                    cadastrarEdificio();
                    break;

                case 3:
                    consultarApartamento();
                    break;

                case 4:
                    alterarDadosApartamento();
                    pausar();
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção Inválida!!!");
                    break;
            }
        } while(op != 0);
    }

    public void menuClientes(){
        int op;
        do {
            limparConsole();
            System.out.println("\n========================================");
            System.out.println("          GESTÃO DE CLIENTES");
            System.out.println("========================================");
            System.out.println(" 1 - Cadastrar Novo Cliente");
            System.out.println(" 2 - Buscar Cliente (por CPF)");
            System.out.println(" 3 - Listar Todos os Clientes");
            System.out.println(" 4 - Editar Dados de Contato");
            System.out.println(" 0 - Voltar ao Menu Anterior");
            System.out.println("----------------------------------------");
            System.out.print("Escolha uma opção: ");

            op = scan.nextInt();
            scan.nextLine(); // Limpa o buffer

            switch(op) {
                case 1:
                    cadastrarCliente();
                    break;
                case 2:
                    buscarCliente();
                    break;
                case 3:
                    listarClientes();
                    break;
                case 4:
                    editarCliente();
                    break;
                case 0: break;
                default: System.out.println("Opção inválida!"); pausar();
            }
        } while (op != 0);
    }

    public void fecharVenda(){
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.println("\n========================================");
        System.out.println("          EFETUAR NOVA VENDA");
        System.out.println("========================================");
        System.out.print("Digite o ID do Edificio: ");
        int idEdificio = scan.nextInt();
        Edificio edAtual = service.buscarEdificioPorId(idEdificio);
        scan.nextLine();

        filtroReservados(idEdificio);
        System.out.println("Insira o número do andar que deseja:");
        int numAndar = scan.nextInt();
        Andar anAtual = service.buscarAndarNoEdificio(edAtual, numAndar);
        scan.nextLine();

        System.out.println("Insira o numero do apt que deseja vender:");
        int numApt = scan.nextInt();
        Apartamento aptAtual = service.buscarApartamentoNoAndar(anAtual, numApt);
        scan.nextLine();

        System.out.println("Insira o CPF do Cliente:");
        String cpfCliente = scan.nextLine();
        if(validar.validarCPF(cpfCliente)){
            System.out.println("CPF inválido!!!");
            pausar();
            return;
        }
        Cliente clienteAtual = service.buscaCliente(cpfCliente);

        System.out.println("Insira a quantia do desconto:");
        double desconto = scan.nextDouble();
        scan.nextLine();

        Venda vendaAtual = new Venda(autenticacaoService.getVendedorAtual(), aptAtual, clienteAtual, desconto);

        System.out.println("\n========================================");
        System.out.println("           COMPROVANTE DE VENDA");
        System.out.println("========================================");

        System.out.println(" > IMÓVEL:");
        System.out.println("   Apartamento: " + vendaAtual.getApartamento().getNumero());
        System.out.println("   Edifício:    " + edAtual.getNome());

        System.out.println("\n > ENVOLVIDOS:");
        System.out.println("   Vendedor: " + vendaAtual.getVendedor().getNome());
        System.out.println("   Cliente:  " + vendaAtual.getCliente().getNome() + " (CPF: " + vendaAtual.getCliente().getCpf() + ")");

        System.out.println("\n > FINANCEIRO E DATA:");
        System.out.printf("   Preço de Tabela: R$ %,.2f\n", vendaAtual.getApartamento().getValorDeVenda());
        System.out.printf("   Valor Fechado:   R$ %,.2f\n", vendaAtual.getValorFinal());

        double economia = vendaAtual.getApartamento().getValorDeVenda() - vendaAtual.getValorFinal();
        if (economia > 0) {
            System.out.printf("   Desconto Total:  R$ %,.2f\n", economia);
        }

        System.out.println("   Data da Venda:   " + vendaAtual.getDataDaVenda());
        System.out.println("========================================\n");
        if(service.fecharNegocio(vendaAtual)){
            System.out.println("Venda bem sucedida!!!");
        }
    }

    private void cadastrarEdificio() {

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
        scan.nextLine();

        // Coleta manual dos dados de cada unidade
        limparConsole();
        System.out.print("Apartamentos por Andar: ");
        int totalAptos = scan.nextInt();
        scan.nextLine();
        for (int i = 1; i <= totalAndares; i++) {
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
        scan.nextLine();
        limparConsole();
        // Service finaliza salvando no repositório
        service.salvarEdificio(edificio);
        System.out.println("\n[SUCESSO] Edifício cadastrado.");
        pausar();
    }

    public void consultarApartamento() {

        System.out.println("\n======================================================================");
        System.out.println("                      LISTA DE EDIFÍCIOS");
        System.out.println("======================================================================");

        System.out.println(service.gerarListaSimplesEdificios());

        System.out.print("Digite o ID do Edifício que deseja consultar: ");
        int idBusca = scan.nextInt();

        Edificio ed = service.buscarEdificioPorId(idBusca);

        if (ed == null) {
            System.out.println("[ERRO] Edifício com ID " + idBusca + " não encontrado.");
            return;
        }

        limparConsole();
        System.out.println("\n--- ANDARES DISPONÍVEIS NO " + ed.getNome().toUpperCase() + " ---");
        for (Andar andar : ed.getAndares()) {
            System.out.printf("Andar: %02dº | Quantidade de Apartamentos: %d\n",
                    andar.getNumero(), andar.getApartamentos().size());
        }

        System.out.print("\nDigite o número do andar que deseja ver os detalhes: ");
        int numAndarBusca = scan.nextInt();
        scan.nextLine();

        Andar andarEscolhido = service.buscarAndarNoEdificio(ed, numAndarBusca);

        if (andarEscolhido == null) {
            System.out.println("[ERRO] Andar " + numAndarBusca + " não encontrado neste edifício.");
            return;
        }
        limparConsole();

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
        pausar();
    }

    private void alterarDadosApartamento(){

        //Selecionar Edifício
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.print("ID do Edifício: ");
        int idEd = scan.nextInt();
        Edificio ed = service.buscarEdificioPorId(idEd);

        if (ed == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return;
        }

        limparConsole();
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

        limparConsole();
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

        menuAlteracao(apto);
    }

    public void menuAlteracao(Apartamento apt){
        int subOp;
        do {
            limparConsole();
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
                    }
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

    public void menuDisponibilidade(){
        limparConsole();

        System.out.println(service.gerarListaSimplesEdificios());

        System.out.println("Seleciona o edificio que deseja verificar a disponibilidade");
        int idEdificio = scan.nextInt();

        System.out.println("\n==== FILTRAR DISPONIBILIDADE ====");
        System.out.println("1 - Apenas DISPONÍVEIS");
        System.out.println("2 - Apenas RESERVADOS");
        System.out.print("Escolha o filtro: ");
        int op = scan.nextInt();
        scan.nextLine();

        switch(op){
            case 1:
                limparConsole();
                filtroDisponiveis(idEdificio);
                break;

            case 2:
                limparConsole();
                filtroReservados(idEdificio);
                break;

            default:
                System.out.println("Erro, opção inválida !!!");
                break;
        }
        pausar();
    }

    public void filtroDisponiveis(int idEdificio){
        Edificio edificio = service.buscarEdificioPorId(idEdificio);

        if(edificio == null) return;
        int ctd = 0;

        System.out.println("\n====================================================================");
        System.out.println("                 LISTAGEM: APARTAMENTOS DISPONÍVEIS");
        System.out.println("====================================================================");
        System.out.println(" APTO | ANDAR | ÁREA (m²) | VALOR DE VENDA (R$)");
        System.out.println("------|-------|-----------|-----------------------------------------");

        for(Andar andarAtual : edificio.getAndares()){
            for(Apartamento aptAtual : andarAtual.getApartamentos()){
                if(aptAtual.getStatus() == StatusApartamento.DISPONIVEL){
                    System.out.printf(new java.util.Locale("pt", "BR"),
                            " %-4d | %02dº   | %-9.1f | R$ %,-20.2f\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero(),
                            aptAtual.getMetragem(),
                            aptAtual.getValorDeVenda()
                    );
                    ctd++;
                }
            }
        }
        if(ctd != 0) {
            System.out.println("--------------------------------------------------------------------");
            System.out.printf(" Total de unidades disponíveis encontradas: %d\n", ctd);
            System.out.println("====================================================================\n");
        } else {
            System.out.println("  [AVISO] Não há unidades disponíveis neste edifício no momento.");
        }

    }

    public void filtroReservados(int idEdificio){
        Edificio edificio = service.buscarEdificioPorId(idEdificio);
        if(edificio == null) return;
        int ctd = 0;

        limparConsole();

        System.out.println("\n==========================================================================================");
        System.out.println("                                LISTAGEM: APARTAMENTOS RESERVADOS");
        System.out.println("==========================================================================================");
        System.out.println(" APTO | ANDAR | ÁREA (m²) | VALOR DE VENDA (R$) | VALOR DO SINAL (R$)                     ");
        System.out.println("------|-------|-----------|---------------------|-----------------------------------------");

        for(Andar andarAtual : edificio.getAndares()){
            for(Apartamento aptAtual : andarAtual.getApartamentos()){
                if(aptAtual.getStatus() == StatusApartamento.RESERVADO){
                    System.out.printf(new java.util.Locale("pt", "BR"),
                            " %-4d | %02dº   | %-9.1f | R$ %,-17.2f | R$ %,-17.2f\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero(),
                            aptAtual.getMetragem(),
                            aptAtual.getValorDeVenda(),
                            aptAtual.getValorSinal()
                    );
                    ctd++;
                }
            }
        }
        if(ctd != 0) {
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.printf(" Total de unidades reservadas encontradas: %d\n", ctd);
            System.out.println("==========================================================================================\n");
        } else {
            System.out.println("  [AVISO] Não há unidades reservadas neste edifício no momento.");
        }
    }

    private void cadastrarCliente(){
        System.out.println("\n========================================");
        System.out.println("       CADASTRO DE CLIENTE TITULAR");
        System.out.println("========================================");
        System.out.print(" > Nome: ");
        String nome = scan.nextLine();

        System.out.print(" > CPF: ");
        String cpf = scan.nextLine();
        if(!validar.validarCPF(cpf)){
            System.out.println("Formato de CPF inválido!!!");
            return;
        }

        System.out.print(" > RG: ");
        String rg = scan.nextLine();

        System.out.println("\n----------- ESTADO CIVIL -----------");
        System.out.println(" 1 - Solteiro(a)");
        System.out.println(" 2 - Casado(a)");
        System.out.println(" 3 - Divorciado(a)");
        System.out.println(" 4 - Viúvo(a)");
        System.out.print(" Escolha a opção: ");
        int op = scan.nextInt();
        scan.nextLine();
        EstadoCivil estado = null;
        Conjuge conjuge = null;

        switch(op){
            case 1:
                estado = EstadoCivil.SOLTEIRO;
                break;

            case 2:
                estado = EstadoCivil.CASADO;
                conjuge = cadastrarConjuge();
                break;

            case 3:
                estado = EstadoCivil.DIVORCIADO;
                break;

            case 4:
                estado = EstadoCivil.VIUVO;
                break;

            default:
                System.out.println("Erro, Opção inválida!!!");
                pausar();
                break;
        }
        Cliente clienteCadastro = null;
        if(conjuge != null){
            clienteCadastro = new Cliente(nome, cpf, rg, estado, conjuge);
        } else {
            clienteCadastro = new Cliente(nome, cpf, rg, estado);
        }

        service.adicionarCliente(clienteCadastro);
        System.out.println("Cadastro bem sucedido");
        pausar();
    }

    private Conjuge cadastrarConjuge(){
        System.out.println("\n========================================");
        System.out.println("       DADOS DO CÔNJUGE");
        System.out.println("========================================");
        System.out.print(" > Nome do Cônjuge: ");
        String nome = scan.nextLine();

        System.out.print(" > CPF do Cônjuge: ");
        String cpf = scan.nextLine();

        System.out.print(" > RG do Cônjuge: ");
        String rg = scan.nextLine();

        Conjuge conjuge = new Conjuge(nome, cpf, rg);

        return conjuge;
    }

    private void buscarCliente(){
        System.out.println("\n========================================");
        System.out.println("           BUSCAR CLIENTE");
        System.out.println("========================================");
        System.out.print("Digite o NOME ou CPF do cliente: ");
        String busca = scan.nextLine();

        Cliente cliente = null;
        cliente = service.buscaCliente(busca);

        if(cliente == null){
            System.out.println("Cliente não encontrado!!! Insira os dados novamente ou cadastre um novo cliente");
            return;
        } else {
            System.out.println("\n----------------------------------------------------------------------");
            System.out.println("                         DADOS DO CLIENTE");
            System.out.println("----------------------------------------------------------------------");

            System.out.printf(" NOME: %-30s | CPF: %s\n", cliente.getNome(), cliente.getCpf());
            System.out.printf(" RG: %-32s | ESTADO CIVIL: %s\n", cliente.getRg(), cliente.getEstadoCivil());

            if (cliente.getConjuge() != null) {
                System.out.println(" > CÔNJUGE VINCULADO:");
                System.out.printf("   Nome: %-28s | CPF: %s\n",
                        cliente.getConjuge().getNome(),
                        cliente.getConjuge().getCpf());
                System.out.printf("   RG: %s\n", cliente.getConjuge().getRg());
            }
            System.out.println("----------------------------------------------------------------------\n");
        }
        pausar();
    }

    private void editarCliente(){
        System.out.println("\n========================================");
        System.out.println("           EDITAR CLIENTE");
        System.out.println("========================================");
        System.out.print("Digite o NOME ou CPF do cliente que deseja editar: ");
        String cD = scan.nextLine();

        Cliente clienteMod = service.buscaCliente(cD);

        if(clienteMod == null){
            System.out.println("\n[ERRO] Cliente não encontrado!");
            System.out.println("Certifique-se de que o nome ou CPF está correto.");
            pausar();
            return;
        }

        int op;
        do {
            System.out.println("\n--- EDITANDO: " + clienteMod.getNome().toUpperCase() + " ---");
            System.out.println(" 1. Nome            | Atual: " + clienteMod.getNome());
            System.out.println(" 2. CPF             | Atual: " + clienteMod.getCpf());
            System.out.println(" 3. RG              | Atual: " + clienteMod.getRg());
            System.out.println(" 4. Estado Civil    | Atual: " + clienteMod.getEstadoCivil());

            if (clienteMod.getConjuge() != null) {
                System.out.println(" 5. Dados do Cônjuge (" + clienteMod.getConjuge().getNome() + ")");
            } else {
                System.out.println(" 5. Adicionar Cônjuge (Vínculo atual: Nulo)");
            }

            System.out.println(" 0. Voltar/Concluir");
            System.out.println("----------------------------------------");
            System.out.print("Escolha o que deseja alterar: ");
            op = scan.nextInt();
            scan.nextLine();

            switch (op) {
                case 1:
                    System.out.print(" > Novo Nome: ");
                    String novoNome = scan.nextLine();
                    clienteMod.setNome(novoNome);
                    break;

                case 2:
                    System.out.print(" > Novo CPF: ");
                    String novoCPF = scan.nextLine();
                    clienteMod.setCpf(novoCPF);
                    break;

                case 3:
                    System.out.print(" > Novo RG: ");
                    String novoRG = scan.nextLine();
                    clienteMod.setRg(novoRG);
                    break;

                case 4:
                    menuEstadoCivil(clienteMod);
                    break;

                case 5:
                    Conjuge novoConjuge = cadastrarConjuge();
                    clienteMod.setConjugue(novoConjuge);
                    clienteMod.setEstadoCivil(EstadoCivil.CASADO);
                    break;
            }
        } while(op != 0);

        System.out.println("\n[OK] Informação atualizada com sucesso!");
        pausar();

    }

    private void menuEstadoCivil(Cliente cliente){
        System.out.println("\n========================================");
        System.out.println("         ALTERAR ESTADO CIVIL");
        System.out.println("========================================");
        System.out.printf(" Estado atual: %s\n", cliente.getEstadoCivil());
        System.out.println("----------------------------------------");
        System.out.println(" 1 - Solteiro(a)");
        System.out.println(" 2 - Casado(a)");
        System.out.println(" 3 - Divorciado(a)");
        System.out.println(" 4 - Viúvo(a)");
        System.out.println("----------------------------------------");
        System.out.print(" Escolha o novo estado: ");
        int op = scan.nextInt();
        scan.nextLine();

        switch(op){
            case 1:
                cliente.setEstadoCivil(EstadoCivil.SOLTEIRO);
                cliente.setConjugue(null);
                break;

            case 2:
                cliente.setEstadoCivil(EstadoCivil.CASADO);
                cliente.setConjugue(cadastrarConjuge());
                break;

            case 3:
                cliente.setEstadoCivil(EstadoCivil.DIVORCIADO);
                break;

            case 4:
                cliente.setEstadoCivil(EstadoCivil.VIUVO);
                break;

            default:
                System.out.println("Opção Inválida!!!");
                pausar();
                break;
        }

    }

    private void listarClientes(){
        System.out.println(service.gerarRelatorioCliente());
        pausar();
    }

    private void limparConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
        // \033[H move o cursor para o início e \033[2J limpa a tela
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pausar(){
        System.out.println("\nPressione ENTER para continuar...");
        scan.nextLine();
    }
}
