package view;

import model.*;
import service.AutenticacaoService;
import service.ImobiliariaService;
import validation.Validar;

import javax.naming.ldap.UnsolicitedNotification;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.util.Scanner;

//alterar as funções das linhas 619 e 460, caso houver alguma dúvida é só perguntar no grupo ou no privado(Marco), caso precise de alguma função que não exista no service adicione um comentário e explique o que ela precisa fazer, se caso precisar dela para o código funcionar crie um esqueleto que retorna null para testes
//
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
            System.out.println("5 - Adicionar Apartamento no Edificio");
            System.out.println("6 - Atualizar status de Apartamento");
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

                case 5:
                    adicionarApartamento();
                    break;

                case 6:
                    atualizarStatusApartamento();

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

        if (edAtual == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            pausar();
            return;
        }

        filtroReservados(idEdificio);
        System.out.println("Insira o número do andar que deseja:");
        int numAndar = scan.nextInt();
        Andar anAtual = service.buscarAndarNoEdificio(edAtual, numAndar);
        scan.nextLine();

        if (anAtual == null) {
            System.out.println("[ERRO] Andar não encontrado neste edifício.");
            pausar();
            return;
        }

        System.out.println("Insira o numero do apt que deseja vender:");
        int numApt = scan.nextInt();
        Apartamento aptAtual = service.buscarApartamentoNoAndar(anAtual, numApt);
        scan.nextLine();

        if (aptAtual == null) {
            System.out.println("[ERRO] Apartamento não encontrado neste andar.");
            pausar();
            return;
        }

        System.out.println("Insira o CPF do Cliente:");
        String cpfCliente = scan.nextLine();
        if(!validar.validarCPF(cpfCliente)){
            System.out.println("CPF inválido!!!");
            pausar();
            return;
        }
        Cliente clienteAtual = service.buscaCliente(cpfCliente);

        if (clienteAtual == null) {
            System.out.println("[ERRO] Cliente não encontrado!!!");
            pausar();
            return;
        }

        double desconto = 0;
        System.out.println("Deseja aplicar desconto na venda? (S/N):");
        String op = scan.nextLine();
        if(op.equals("S")) {
            System.out.println("Insira a quantia do desconto:");
            desconto = scan.nextDouble();
            scan.nextLine();

            if (!validar.validarDesconto(desconto)) {
                System.out.println("[ERRO] Desconto inválido. Informe um percentual > 0.");
                pausar();
                return;
            }
        }

        if (autenticacaoService.getVendedorAtual() == null) {
            System.out.println("[ERRO] Vendedor não autenticado.");
            pausar();
            return;
        }

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

    private void cadastrarEdificio()
    {

        System.out.println("\n--- CADASTRO DE EDIFÍCIO ---");
        int id = service.getIDgenerator();
        service.setIDgenerator(service.getIDgenerator()+1);

        System.out.print("Nome: ");
        String nome = scan.nextLine();

        System.out.print("Endereço: ");
        String endereco = scan.nextLine();

        // Service inicia a criação
        Edificio edificio = service.iniciarNovoEdificio(id, nome, endereco);

        // Service finaliza salvando no repositório
        service.salvarEdificio(edificio);
        System.out.println("\n[SUCESSO] Edifício cadastrado.");
        pausar();
    }

    private void adicionarApartamento(){
        int idED;
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.println("Insira o ID do edificio que deseja adicionar o apartamento:");
        idED = scan.nextInt();

        Edificio ed = service.buscarEdificioPorId(idED);

        if(ed == null){
            System.out.println("EDIFICIO NÃO ENCONTRADO!!!");
            return;
        }

        int numAndar;

        System.out.println("Insira o andar que deseja inserir o apartamento: ");
        numAndar = scan.nextInt();
        scan.nextLine();

        Apartamento aptNovo;

        System.out.println("Insira a metragem(m²) do apartamento: ");
        double metragem = scan.nextDouble();
        scan.nextLine();

        System.out.println("Insira a quantidade de quartos: ");
        int qtdQuartos = scan.nextInt();
        scan.nextLine();

        System.out.println("Insira a quantidade de banheiros: ");
        int qtdBanheiros = scan.nextInt();
        scan.nextLine();

        System.out.println("Insira o valor do apartamento: ");
        double precoApt = scan.nextDouble();
        scan.nextLine();

        service.vincularApartamento(ed, numAndar, metragem, precoApt, qtdQuartos, qtdBanheiros );

        service.getDados().gravarArquivo();

        System.out.println("\n[SUCESSO] Apartamento adicionado ao edificio !");

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

    public Apartamento encontrarApartamento(){
        //Selecionar Edifício
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.print("ID do Edifício: ");
        int idEd = scan.nextInt();
        Edificio ed = service.buscarEdificioPorId(idEd);

        if (ed == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return null;
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
            return null;
        }

        limparConsole();
        //Selecionar Apartamento
        System.out.println("\n--- UNIDADES NO " + numAndar + "º ANDAR ---");
        for (Apartamento a : andar.getApartamentos()) {
            System.out.print("[" + a.getNumero() + "] ");
        }
        System.out.print("\nNúmero do Apartamento: ");
        int numApto = scan.nextInt();

        Apartamento apto = service.buscarApartamentoNoAndar(andar, numApto);

        if (apto == null) {
            System.out.println("[ERRO] Apartamento não encontrado.");
            return null;
        }

        return apto;
    }

    public void atualizarStatusApartamento(){

        Apartamento aptAtualizar = encontrarApartamento();

        if(aptAtualizar == null){
            System.out.println("Apartamento não encontrado!!!");
            pausar();
            return;
        }
        Edificio ed = service.procurarEdificioApartamento(aptAtualizar);

        System.out.println(formatarConfirmacaoImovel(ed, aptAtualizar));
        System.out.println("Este é o apartamento que deseja reservar? (S/N):");
        String op = scan.nextLine();

        //Finalizar a reserva do edificio

    }

    public String formatarConfirmacaoImovel(Edificio ed, Apartamento apt) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n====================================================\n");
        sb.append("             CONFIRMAÇÃO DO IMÓVEL\n");
        sb.append("====================================================\n");

        // Dados do Edifício
        sb.append(String.format(" Edifício: %s\n", ed.getNome().toUpperCase()));
        sb.append(String.format(" Endereço: %s\n", ed.getEndereco()));

        sb.append("----------------------------------------------------\n");

        // Dados da Unidade
        sb.append(String.format(" Unidade:  Apto %d\n", apt.getNumero()));
        sb.append(String.format(" Andar:   %dº Andar\n", apt.getAndar()));
        sb.append(String.format(" Valor:    R$ %,.2f\n", apt.getValorDeVenda()));

        sb.append("====================================================\n");

        return sb.toString();
    }

    private void alterarDadosApartamento(){

        Apartamento apto = encontrarApartamento();

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

            // Exibe o interessado apenas se estiver reservado
            if (apt.getStatus() == StatusApartamento.RESERVADO) {
                String interessado = (apt.getClienteInteressado() == null || apt.getClienteInteressado() == null) // alterar esta parte para se adequar a classe Cliente
                        ? "NÃO VINCULADO" : apt.getClienteInteressado().getNome();
                System.out.printf("    > Interessado | CPF: %s\n", interessado);
            }

            System.out.printf(" 2. Valor Venda   | Atual: R$ %,.2f\n", apt.getValorDeVenda());
            System.out.printf(" 3. Valor Sinal   | Atual: R$ %,.2f\n", apt.getValorSinal());
            System.out.println(" 0. Concluir/Voltar");
            System.out.println("----------------------------------------------------");
            System.out.print("Escolha o que alterar: ");

            subOp = scan.nextInt();
            scan.nextLine();

            switch (subOp) {
                case 1:
                    System.out.print("Novo Status (1-DISPONIVEL, 2-RESERVADO): ");
                    int st = scan.nextInt();
                    scan.nextLine();

                    String cpf = "";
                    if(st == 2){
                        System.out.print("Insira o CPF do interessado: ");
                        cpf = scan.nextLine();

                        System.out.print("Insira o valor do sinal (R$): ");
                        double sinal = scan.nextDouble();
                        scan.nextLine();
                        apt.setValorSinal(sinal);
                    } else if (st == 1) {
                        apt.setValorSinal(0);
                    }

                    if(!service.atualizarStatus(apt, st, cliente)){
                        System.out.println("[ERRO] Opção de status inválida!");
                        pausar();
                    }
                    break;

                case 2:
                    System.out.print("Novo Valor de Venda: R$ ");
                    double novoValor = scan.nextDouble();
                    scan.nextLine();
                    apt.setValorDeVenda(novoValor);
                    break;

                case 3:
                    System.out.print("Novo Valor de Sinal: R$ ");
                    double novoSinal = scan.nextDouble();
                    scan.nextLine();
                    apt.setValorSinal(novoSinal);
                    break;

                case 0:
                    System.out.println("[OK] Alterações finalizadas.");
                    break;

                default:
                    System.out.println("Opção inválida.");
                    pausar();
            }
        } while (subOp != 0);

        // Persistir alterações realizadas
        service.getDados().gravarArquivo();
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

        System.out.println("\n===============================================================================================================");
        System.out.println("                                     LISTAGEM: APARTAMENTOS RESERVADOS");
        System.out.println("===============================================================================================================");
        System.out.println(" APTO | ANDAR | ÁREA (m²) | VALOR DE VENDA (R$) | VALOR DO SINAL (R$)   | CPF INTERESSADO");
        System.out.println("------|-------|-----------|---------------------|-----------------------|--------------------------------------");

        for(Andar andarAtual : edificio.getAndares()){
            for(Apartamento aptAtual : andarAtual.getApartamentos()){
                if(aptAtual.getStatus() == StatusApartamento.RESERVADO){
                    System.out.printf(new java.util.Locale("pt", "BR"),
                            " %-4d | %02dº   | %-9.1f | R$ %,-17.2f | R$ %,-19.2f | %-15s\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero(),
                            aptAtual.getMetragem(),
                            aptAtual.getValorDeVenda(),
                            aptAtual.getValorSinal(),
                            (aptAtual.getClienteInteressado() == null || aptAtual.getClienteInteressado() == null ? "NÃO INFORMADO" : aptAtual.getClienteInteressado().getNome()) //alterar esta parte para se adequar a classe cliente
                    );
                    ctd++;
                }
            }
        }

        if(ctd != 0) {
            System.out.println("---------------------------------------------------------------------------------------------------------------");
            System.out.printf(" Total de unidades reservadas encontradas: %d\n", ctd);
            System.out.println("===============================================================================================================\n");
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
                return;
        }

        if (estado == null) {
            System.out.println("[ERRO] Estado civil inválido.");
            pausar();
            return;
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
        // Persistir alterações no arquivo JSON
        service.getDados().gravarArquivo();
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
                cliente.setConjugue(null);
                break;

            case 4:
                cliente.setEstadoCivil(EstadoCivil.VIUVO);
                cliente.setConjugue(null);
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
