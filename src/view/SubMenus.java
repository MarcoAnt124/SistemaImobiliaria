package view;

import model.*;
import service.AutenticacaoService;
import service.ImobiliariaService;
import validation.Validar;

import java.util.Scanner;

public class SubMenus {
    private ImobiliariaService service;
    private Scanner scan;
    private AutenticacaoService autenticacaoService;

    public SubMenus(ImobiliariaService service, Scanner scan, AutenticacaoService autenticacaoService) {
        this.service = service;
        this.scan = scan;
        this.autenticacaoService = autenticacaoService;
    }

    public void listarVendas(){
        System.out.println(service.gerarListaVendas());
        pausar();
    }

    public void menuImoveis(){
        int op;
        do{
            limparConsole();
            System.out.println("\n==== Gestão de Imóveis ====");
            System.out.println("1 - Ver Disponibilidade dos Apartamentos");
            System.out.println("2 - Cadastrar novo Edificio");
            System.out.println("3 - Consultar Apartamento Especifico");
            System.out.println("4 - Alterar dados de Apartamento");
            System.out.println("5 - Adicionar Apartamento no Edificio");
            System.out.println("6 - Reservar / Liberar Apartamento");
            System.out.println("7 - Atualizar Estágio da Obra e Valores");
            System.out.println("8 - Listar Apartamentos Vendidos");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            op = lerInt();

            switch(op){
                case 1: menuDisponibilidade(); break;
                case 2: cadastrarEdificio(); break;
                case 3: consultarApartamento(); break;
                case 4: alterarDadosApartamento(); break;
                case 5: adicionarApartamento(); break;
                case 6: atualizarStatusApartamento(); break;
                case 7: atualizarEstagioDaObraMenu(); break;
                case 8: menuVendidos(); break;
                case 0: System.out.println("Saindo..."); break;
                default: System.out.println("Opção Inválida!!!"); break;
            }
        } while(op != 0);
    }

    private void atualizarEstagioDaObraMenu() {
        limparConsole();
        System.out.println("==== ATUALIZAR ESTÁGIO DA OBRA E VALORES ====");
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.print("Digite o ID do Edifício desejado: ");
        int idEd = lerInt();

        Edificio ed = service.buscarEdificioPorId(idEd);
        if (ed == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            pausar();
            return;
        }

        System.out.println("\n--- Selecione o Novo Estágio da Obra ---");
        EstagioObra[] estagios = EstagioObra.values();
        for (int i = 0; i < estagios.length; i++) {
            System.out.println((i + 1) + " - " + estagios[i].name());
        }

        System.out.print("Escolha o número do estágio: ");
        int opEstagio = lerInt();

        if (opEstagio < 1 || opEstagio > estagios.length) {
            System.out.println("[ERRO] Opção de estágio inválida.");
            pausar();
            return;
        }

        EstagioObra novoEstagio = estagios[opEstagio - 1];

        boolean estagioAtualizado = service.atualizarEstagioDaObra(idEd, novoEstagio);

        System.out.println("\n--- Atualizando valores das unidades do edifício " + ed.getNome() + " ---");

        int valoresAtualizados = 0;
        for (Andar andar : ed.getAndares()) {
            for (Apartamento apt : andar.getApartamentos()) {
                if (apt.getStatus() == StatusApartamento.VENDIDO) {
                    continue;
                }
                System.out.printf("\nApto: %d | Andar: %02dº | Valor Atual: %s\n",
                        apt.getNumero(), andar.getNumero(), Validar.formatarValorReais(apt.getValorDeVenda()));
                System.out.print("Digite o NOVO valor de venda (R$): ");
                double novoValor = lerDouble();

                if (!Validar.validarValorPositivo(novoValor)) {
                    System.out.println("[AVISO] Valor inválido ignorado para o apto " + apt.getNumero());
                    continue;
                }
                service.atualizarValorApartamento(apt, novoValor);
                valoresAtualizados++;
            }
        }

        if (valoresAtualizados > 0) {
            service.getDados().gravarArquivo();
        }

        if (estagioAtualizado || valoresAtualizados > 0) {
            System.out.println("\n[SUCESSO] Estágio da obra e/ou valores atualizados com sucesso!");
        } else {
            System.out.println("\n[AVISO] Nenhuma alteração foi aplicada.");
        }
        pausar();
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

            op = lerInt();

            switch(op) {
                case 1: cadastrarCliente(); break;
                case 2: buscarCliente(); break;
                case 3: listarClientes(); break;
                case 4: editarCliente(); break;
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
        int idEdificio = lerInt();
        Edificio edAtual = service.buscarEdificioPorId(idEdificio);

        if (edAtual == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            pausar();
            return;
        }

        filtroReservados(idEdificio);
        System.out.println("Insira o número do andar que deseja:");
        int numAndar = lerInt();
        Andar anAtual = service.buscarAndarNoEdificio(edAtual, numAndar);

        if (anAtual == null) {
            System.out.println("[ERRO] Andar não encontrado neste edifício.");
            pausar();
            return;
        }

        System.out.println("Insira o numero do apt que deseja vender:");
        int numApt = lerInt();
        Apartamento aptAtual = service.buscarApartamentoNoAndar(anAtual, numApt);

        if (aptAtual == null) {
            System.out.println("[ERRO] Apartamento não encontrado neste andar.");
            pausar();
            return;
        }

        if (aptAtual.getStatus() == StatusApartamento.VENDIDO) {
            System.out.println("[ERRO] Este apartamento já foi vendido.");
            pausar();
            return;
        }

        if (aptAtual.getStatus() != StatusApartamento.RESERVADO) {
            System.out.println("[ERRO] O apartamento precisa estar RESERVADO para efetuar a venda.");
            pausar();
            return;
        }

        System.out.println("Insira o CPF do Cliente:");
        String cpfCliente = scan.nextLine();

        Cliente clienteAtual = service.buscaCliente(cpfCliente);

        if (clienteAtual == null) {
            System.out.println("[ERRO] Cliente não encontrado!!!");
            pausar();
            return;
        }

        if (!service.podeEfetuarVenda(aptAtual, clienteAtual)) {
            System.out.println("[ERRO] O cliente informado não corresponde à reserva deste apartamento.");
            pausar();
            return;
        }

        double desconto = 0;
        System.out.println("Deseja aplicar desconto na venda? (S/N):");
        String op = scan.nextLine();
        if(op.equalsIgnoreCase("S")) {
            System.out.println("Insira o percentual de desconto (máximo 100%):");
            desconto = lerDouble();

            if (!Validar.validarDesconto(desconto)) {
                System.out.println("[ERRO] Desconto inválido. Informe um valor entre 0% (exclusivo) e 100%.");
                pausar();
                return;
            }
        }

        if (autenticacaoService.getVendedorAtual() == null) {
            System.out.println("[ERRO] Vendedor não autenticado.");
            pausar();
            return;
        }

        Venda vendaAtual = new Venda(autenticacaoService.getVendedorAtual(), aptAtual, clienteAtual,
                edAtual.getId(), desconto);

        System.out.print("\nConfirmar e efetuar a venda? (S/N): ");
        String confirmacao = scan.nextLine();
        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println("Venda cancelada pelo usuário.");
            pausar();
            return;
        }

        if (service.fecharNegocio(vendaAtual)) {
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
            System.out.println("========================================");
            System.out.println("\nVenda bem sucedida!!!");
        } else {
            System.out.println("[ERRO] Não foi possível concluir a venda. Verifique os dados e tente novamente.");
        }
        pausar();
    }

    private void cadastrarEdificio() {
        System.out.println("\n--- CADASTRO DE EDIFÍCIO ---");
        int id = service.getIDgenerator();

        System.out.print("Nome: ");
        String nome = scan.nextLine();

        System.out.print("Endereço: ");
        String endereco = scan.nextLine();

        if (!Validar.textoNaoVazio(nome) || !Validar.textoNaoVazio(endereco)) {
            System.out.println("\n[ERRO] Nome e endereço são obrigatórios.");
            pausar();
            return;
        }

        Edificio edificio = service.iniciarNovoEdificio(id, nome.trim(), endereco.trim());
        if (service.salvarEdificio(edificio)) {
            service.setIDgenerator(id + 1);
            System.out.println("\n[SUCESSO] Edifício cadastrado.");
        } else {
            System.out.println("\n[ERRO] Falha ao cadastrar edifício (ID duplicado, dados inválidos ou erro ao salvar).");
        }
        pausar();
    }

    private void adicionarApartamento(){
        int idED;
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.println("Insira o ID do edificio que deseja adicionar o apartamento:");
        idED = lerInt();

        Edificio ed = service.buscarEdificioPorId(idED);

        if(ed == null){
            System.out.println("EDIFICIO NÃO ENCONTRADO!!!");
            pausar();
            return;
        }

        System.out.println("Insira o andar que deseja inserir o apartamento: ");
        int numAndar = lerInt();
        if (numAndar <= 0) {
            System.out.println("[ERRO] O número do andar deve ser maior que zero.");
            pausar();
            return;
        }

        System.out.println("Insira a metragem(m²) do apartamento: ");
        double metragem = lerDouble();
        if (!Validar.validarValorPositivo(metragem)) {
            System.out.println("[ERRO] Metragem deve ser maior que zero.");
            pausar();
            return;
        }

        System.out.println("Insira a quantidade de quartos: ");
        int qtdQuartos = lerInt();
        if (qtdQuartos < 0) {
            System.out.println("[ERRO] Quantidade de quartos inválida.");
            pausar();
            return;
        }

        System.out.println("Insira a quantidade de banheiros: ");
        int qtdBanheiros = lerInt();
        if (qtdBanheiros < 0) {
            System.out.println("[ERRO] Quantidade de banheiros inválida.");
            pausar();
            return;
        }

        System.out.println("Insira o valor do apartamento: ");
        double precoApt = lerDouble();
        if (!Validar.validarValorPositivo(precoApt)) {
            System.out.println("[ERRO] Valor do apartamento deve ser maior que zero.");
            pausar();
            return;
        }

        if (!service.vincularApartamento(ed, numAndar, metragem, precoApt, qtdQuartos, qtdBanheiros)) {
            System.out.println("[ERRO] Não foi possível vincular o apartamento (verifique o andar).");
            pausar();
            return;
        }

        Andar andarApt = service.buscarAndarNoEdificio(ed, numAndar);
        int numAptGerado = 0;
        if (andarApt != null && !andarApt.getApartamentos().isEmpty()) {
            Apartamento ultimo = andarApt.getApartamentos().get(andarApt.getApartamentos().size() - 1);
            numAptGerado = ultimo.getNumero();
        }

        if (!service.getDados().gravarArquivo()) {
            if (numAptGerado > 0) {
                service.removerUltimoApartamento(ed, numAndar, numAptGerado);
            }
            System.out.println("[ERRO] Falha ao salvar o apartamento no arquivo de dados.");
            pausar();
            return;
        }
        System.out.println("\n[SUCESSO] Apartamento adicionado ao edificio !");
        pausar();
    }

    public void consultarApartamento() {
        System.out.println("\n======================================================================");
        System.out.println("                      LISTA DE EDIFÍCIOS");
        System.out.println("======================================================================");

        System.out.println(service.gerarListaSimplesEdificios());

        System.out.print("Digite o ID do Edifício que deseja consultar: ");
        int idBusca = lerInt();

        Edificio ed = service.buscarEdificioPorId(idBusca);

        if (ed == null) {
            System.out.println("[ERRO] Edifício com ID " + idBusca + " não encontrado.");
            pausar();
            return;
        }

        limparConsole();
        System.out.println("\n--- ANDARES DISPONÍVEIS NO " + ed.getNome().toUpperCase() + " ---");
        for (Andar andar : ed.getAndares()) {
            System.out.printf("Andar: %02dº | Quantidade de Apartamentos: %d\n",
                    andar.getNumero(), andar.getApartamentos().size());
        }

        System.out.print("\nDigite o número do andar que deseja ver os detalhes: ");
        int numAndarBusca = lerInt();

        Andar andarEscolhido = service.buscarAndarNoEdificio(ed, numAndarBusca);

        if (andarEscolhido == null) {
            System.out.println("[ERRO] Andar " + numAndarBusca + " não encontrado neste edifício.");
            pausar();
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
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.print("ID do Edifício: ");
        int idEd = lerInt();

        Edificio ed = service.buscarEdificioPorId(idEd);

        if (ed == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return null;
        }

        limparConsole();
        System.out.println("\n--- ANDARES DO " + ed.getNome().toUpperCase() + " ---");
        for (Andar a : ed.getAndares()) {
            System.out.printf("Andar: %02dº | Unidades: %d\n", a.getNumero(), a.getApartamentos().size());
        }

        System.out.print("Número do Andar: ");
        int numAndar = lerInt();

        Andar andar = service.buscarAndarNoEdificio(ed, numAndar);

        if (andar == null) {
            System.out.println("[ERRO] Andar não encontrado.");
            return null;
        }

        limparConsole();
        System.out.println("\n--- UNIDADES NO " + numAndar + "º ANDAR ---");
        for (Apartamento a : andar.getApartamentos()) {
            System.out.print("[" + a.getNumero() + "] ");
        }
        System.out.print("\nNúmero do Apartamento: ");
        int numApto = lerInt();

        Apartamento apto = service.buscarApartamentoNoAndar(andar, numApto);

        if (apto == null) {
            System.out.println("[ERRO] Apartamento não encontrado.");
            return null;
        }

        return apto;
    }

    public void atualizarStatusApartamento(){
        System.out.println("\n==== RESERVAR / LIBERAR APARTAMENTO ====");
        Apartamento aptAtualizar = encontrarApartamento();

        if(aptAtualizar == null){
            System.out.println("Apartamento não encontrado!!!");
            pausar();
            return;
        }
        Edificio ed = service.procurarEdificioApartamento(aptAtualizar);

        if (ed == null) {
            System.out.println("[ERRO] Edifício do apartamento não encontrado.");
            pausar();
            return;
        }

        System.out.println(formatarConfirmacaoImovel(ed, aptAtualizar));

        if (aptAtualizar.getStatus() == StatusApartamento.VENDIDO) {
            System.out.println(" [!] STATUS ATUAL: Este apartamento já foi VENDIDO e não pode ser alterado.");
            pausar();
            return;
        } else if (aptAtualizar.getStatus() == StatusApartamento.RESERVADO) {
            Cliente clienteSalvo = aptAtualizar.getClienteInteressado();
            String info = (clienteSalvo != null) ? clienteSalvo.getNome() + " (CPF: " + clienteSalvo.getCpf() + ")" : "NÃO INFORMADO";
            System.out.println(" [!] STATUS ATUAL: Este apartamento já está RESERVADO para: " + info);
        } else {
            System.out.println(" [!] STATUS ATUAL: Este apartamento está DISPONÍVEL.");
        }

        System.out.print("\nDeseja alterar o status deste apartamento? (S/N): ");
        String op = scan.nextLine();

        if (op.equalsIgnoreCase("S")) {
            System.out.print("Novo Status (1-DISPONIVEL, 2-RESERVADO): ");
            int st = lerInt();

            Cliente cliente = null;

            if (st == 2) {
                if (aptAtualizar.getStatus() == StatusApartamento.RESERVADO) {
                    System.out.println("[ERRO] Apartamento já reservado. Libere-o (opção 1) antes de reservar novamente.");
                    pausar();
                    return;
                }
                System.out.print("Insira o CPF do interessado: ");
                String cpf = scan.nextLine();

                cliente = service.buscaCliente(cpf);
                if (cliente == null) {
                    System.out.println("\n[AVISO] Cliente não está cadastrado! Iniciando cadastro de novo cliente...");
                    cliente = cadastrarCliente();

                    if (cliente == null) {
                        System.out.println("[ERRO] Cadastro cancelado ou falhou. Operação de reserva abortada.");
                        pausar();
                        return;
                    }
                }

                if (!service.atualizarStatus(aptAtualizar, st, cliente)) {
                    System.out.println("\n[ERRO] Não foi possível reservar o apartamento.");
                    pausar();
                    return;
                }

                System.out.print("Insira o valor do sinal (R$): ");
                double sinal = lerDouble();
                if (!Validar.validarValorPositivo(sinal)) {
                    aptAtualizar.setStatus(StatusApartamento.DISPONIVEL);
                    aptAtualizar.setClienteInteressado(null);
                    System.out.println("[ERRO] Valor do sinal inválido. Reserva cancelada.");
                    pausar();
                    return;
                }
                aptAtualizar.setValorSinal(sinal);
                service.getDados().gravarArquivo();
                System.out.println("\n[SUCESSO] Apartamento reservado com sucesso!");
            } else if (st == 1) {
                if (service.atualizarStatus(aptAtualizar, st, cliente)) {
                    System.out.println("\n[SUCESSO] Apartamento liberado com sucesso!");
                    service.getDados().gravarArquivo();
                } else {
                    System.out.println("\n[ERRO] Não foi possível liberar o apartamento.");
                }
            } else {
                System.out.println("\n[ERRO] Opção de status inválida. Use 1 ou 2.");
            }
        } else {
            System.out.println("Operação de alteração cancelada.");
        }
        pausar();
    }

    public String formatarConfirmacaoImovel(Edificio ed, Apartamento apt) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====================================================\n");
        sb.append("             CONFIRMAÇÃO DO IMÓVEL\n");
        sb.append("====================================================\n");
        sb.append(String.format(" Edifício: %s\n", ed.getNome().toUpperCase()));
        sb.append(String.format(" Endereço: %s\n", ed.getEndereco()));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" Unidade:  Apto %d\n", apt.getNumero()));
        sb.append(String.format(" Andar:   %dº Andar\n", apt.getAndar()));
        sb.append(String.format(" Valor:    R$ %,.2f\n", apt.getValorDeVenda()));
        sb.append("====================================================\n");
        return sb.toString();
    }

    private void alterarDadosApartamento(){
        Apartamento apto = encontrarApartamento();
        if (apto == null) {
            pausar();
            return;
        }
        menuAlteracao(apto);
        pausar();
    }

    public void menuAlteracao(Apartamento apt){
        if (apt.getStatus() == StatusApartamento.VENDIDO) {
            System.out.println("[ERRO] Apartamento vendido não pode ser alterado.");
            return;
        }

        int subOp;
        do {
            limparConsole();
            System.out.println("\n====================================================");
            System.out.println("       EDITANDO APARTAMENTO " + apt.getNumero());
            System.out.println("====================================================");
            System.out.printf(" 1. Status        | Atual: %s\n", apt.getStatus());

            if (apt.getStatus() == StatusApartamento.RESERVADO) {
                String infoInteressado = "NÃO VINCULADO";
                Cliente clienteSalvo = apt.getClienteInteressado();

                if (clienteSalvo != null) {
                    infoInteressado = clienteSalvo.getNome() + " (CPF: " + clienteSalvo.getCpf() + ")";
                }
                System.out.printf("    > Interessado | %s\n", infoInteressado);
            }

            System.out.printf(" 2. Valor Venda   | Atual: R$ %,.2f\n", apt.getValorDeVenda());
            System.out.printf(" 3. Valor Sinal   | Atual: R$ %,.2f\n", apt.getValorSinal());
            System.out.println(" 0. Concluir/Voltar");
            System.out.println("----------------------------------------------------");
            System.out.print("Escolha o que alterar: ");

            subOp = lerInt();

            switch (subOp) {
                case 1:
                    System.out.print("Novo Status (1-DISPONIVEL, 2-RESERVADO): ");
                    int st = lerInt();

                    Cliente clienteParaVincular = null;
                    if (st == 2) {
                        if (apt.getStatus() == StatusApartamento.RESERVADO) {
                            System.out.println("[ERRO] Já reservado. Libere antes de reservar novamente.");
                            pausar();
                            break;
                        }
                        System.out.print("Insira o CPF do interessado: ");
                        String cpf = scan.nextLine();

                        clienteParaVincular = service.buscaCliente(cpf);
                        if (clienteParaVincular == null) {
                            System.out.println("\n[AVISO] Cliente não está cadastrado! Iniciando cadastro...");
                            clienteParaVincular = cadastrarCliente();
                            if (clienteParaVincular == null) {
                                System.out.println("[ERRO] Falha ao recuperar cliente cadastrado. Abortando edição.");
                                pausar();
                                break;
                            }
                        }

                        if (!service.atualizarStatus(apt, st, clienteParaVincular)) {
                            System.out.println("[ERRO] Não foi possível reservar o apartamento.");
                            pausar();
                            break;
                        }

                        System.out.print("Insira o valor do sinal (R$): ");
                        double sinal = lerDouble();
                        if (!Validar.validarValorPositivo(sinal)) {
                            apt.setStatus(StatusApartamento.DISPONIVEL);
                            apt.setClienteInteressado(null);
                            System.out.println("[ERRO] Valor do sinal inválido. Reserva cancelada.");
                            pausar();
                            break;
                        }
                        apt.setValorSinal(sinal);
                    } else if (st == 1) {
                        if (!service.atualizarStatus(apt, st, clienteParaVincular)) {
                            System.out.println("[ERRO] Não foi possível liberar o apartamento.");
                            pausar();
                        }
                    } else {
                        System.out.println("[ERRO] Opção de status inválida. Use 1 ou 2.");
                        pausar();
                    }
                    break;

                case 2:
                    System.out.print("Novo Valor de Venda: R$ ");
                    double novoValor = lerDouble();
                    if (!Validar.validarValorPositivo(novoValor)) {
                        System.out.println("[ERRO] Valor de venda deve ser maior que zero.");
                        pausar();
                        break;
                    }
                    apt.setValorDeVenda(novoValor);
                    break;

                case 3:
                    if (apt.getStatus() != StatusApartamento.RESERVADO) {
                        System.out.println("[ERRO] Sinal só pode ser alterado em apartamento RESERVADO.");
                        pausar();
                        break;
                    }
                    System.out.print("Novo Valor de Sinal: R$ ");
                    double novoSinal = lerDouble();
                    if (!Validar.validarValorPositivo(novoSinal)) {
                        System.out.println("[ERRO] Valor do sinal deve ser maior que zero.");
                        pausar();
                        break;
                    }
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

        service.getDados().gravarArquivo();
    }

    public void menuDisponibilidade(){
        limparConsole();

        System.out.println(service.gerarListaSimplesEdificios());

        System.out.println("Seleciona o edificio que deseja verificar a disponibilidade");
        int idEdificio = lerInt();

        System.out.println("\n==== FILTRAR DISPONIBILIDADE ====");
        System.out.println("1 - Apenas DISPONÍVEIS");
        System.out.println("2 - Apenas RESERVADOS");
        System.out.println("3 - Apenas VENDIDOS");
        System.out.print("Escolha o filtro: ");
        int op = lerInt();

        switch(op){
            case 1:
                limparConsole();
                filtroDisponiveis(idEdificio);
                break;
            case 2:
                limparConsole();
                filtroReservados(idEdificio);
                break;
            case 3:
                limparConsole();
                filtroVendidos(idEdificio);
                break;
            default:
                System.out.println("Erro, opção inválida !!!");
                break;
        }
        pausar();
    }

    private void menuVendidos() {
        limparConsole();
        System.out.println(service.gerarListaSimplesEdificios());
        System.out.print("Digite o ID do Edifício: ");
        int idEdificio = lerInt();
        filtroVendidos(idEdificio);
        pausar();
    }

    public void filtroVendidos(int idEdificio) {
        Edificio edificio = service.buscarEdificioPorId(idEdificio);
        if (edificio == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return;
        }
        int ctd = 0;

        limparConsole();
        System.out.println("\n===============================================================================================================");
        System.out.println("                                     LISTAGEM: APARTAMENTOS VENDIDOS");
        System.out.println("===============================================================================================================");
        System.out.println(" APTO | ANDAR | ÁREA (m²) | VALOR DE VENDA (R$)");
        System.out.println("------|-------|-----------|-----------------------------------------");

        for (Andar andarAtual : edificio.getAndares()) {
            for (Apartamento aptAtual : andarAtual.getApartamentos()) {
                if (aptAtual.getStatus() == StatusApartamento.VENDIDO) {
                    System.out.printf(new java.util.Locale("pt", "BR"),
                            " %-4d | %02dº   | %-9.1f | %s\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero(),
                            aptAtual.getMetragem(),
                            Validar.formatarValorReais(aptAtual.getValorDeVenda())
                    );
                    ctd++;
                }
            }
        }

        if (ctd != 0) {
            System.out.println("---------------------------------------------------------------------------------------------------------------");
            System.out.printf(" Total de unidades vendidas encontradas: %d\n", ctd);
            System.out.println("===============================================================================================================\n");
        } else {
            System.out.println("  [AVISO] Não há unidades vendidas neste edifício no momento.");
        }
    }

    public void filtroDisponiveis(int idEdificio){
        Edificio edificio = service.buscarEdificioPorId(idEdificio);

        if (edificio == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return;
        }
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
        if (edificio == null) {
            System.out.println("[ERRO] Edifício não encontrado.");
            return;
        }
        int ctd = 0;

        limparConsole();

        System.out.println("\n===============================================================================================================");
        System.out.println("                                     LISTAGEM: APARTAMENTOS RESERVADOS");
        System.out.println("===============================================================================================================");
        System.out.println(" APTO | ANDAR | ÁREA (m²) | VALOR DE VENDA (R$) | VALOR DO SINAL (R$)   | DADOS DO INTERESSADO");
        System.out.println("------|-------|-----------|---------------------|-----------------------|--------------------------------------");

        for(Andar andarAtual : edificio.getAndares()){
            for(Apartamento aptAtual : andarAtual.getApartamentos()){
                if(aptAtual.getStatus() == StatusApartamento.RESERVADO){

                    String infoInteressado = "NÃO INFORMADO";
                    Cliente clienteSalvo = aptAtual.getClienteInteressado();

                    if(clienteSalvo != null){
                        infoInteressado = clienteSalvo.getNome() + " (CPF: " + clienteSalvo.getCpf() + ")";
                    }

                    System.out.printf(new java.util.Locale("pt", "BR"),
                            " %-4d | %02dº   | %-9.1f | R$ %,-17.2f | R$ %,-19.2f | %-15s\n",
                            aptAtual.getNumero(),
                            andarAtual.getNumero(),
                            aptAtual.getMetragem(),
                            aptAtual.getValorDeVenda(),
                            aptAtual.getValorSinal(),
                            infoInteressado
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

    private Cliente cadastrarCliente(){
        System.out.println("\n========================================");
        System.out.println("       CADASTRO DE CLIENTE TITULAR");
        System.out.println("========================================");
        System.out.print(" > Nome: ");
        String nome = scan.nextLine();

        System.out.print(" > CPF: ");
        String cpf = scan.nextLine();

        System.out.print(" > RG: ");
        String rg = scan.nextLine();

        System.out.println("\n----------- ESTADO CIVIL -----------");
        System.out.println(" 1 - Solteiro(a)");
        System.out.println(" 2 - Casado(a)");
        System.out.println(" 3 - Divorciado(a)");
        System.out.println(" 4 - Viúvo(a)");
        System.out.print(" Escolha a opção: ");
        int op = lerInt();
        EstadoCivil estado = null;
        Conjuge conjuge = null;

        switch(op){
            case 1: estado = EstadoCivil.SOLTEIRO; break;
            case 2:
                estado = EstadoCivil.CASADO;
                conjuge = cadastrarConjuge();
                if (conjuge == null) {
                    pausar();
                    return null;
                }
                break;
            case 3: estado = EstadoCivil.DIVORCIADO; break;
            case 4: estado = EstadoCivil.VIUVO; break;
            default: System.out.println("Erro, Opção inválida!!!"); pausar(); return null;
        }

        if (estado == null) {
            System.out.println("[ERRO] Estado civil inválido.");
            pausar();
            return null;
        }
        Cliente clienteCadastro;
        if (!Validar.textoNaoVazio(nome) || !Validar.textoNaoVazio(cpf)) {
            System.out.println("[ERRO] Nome e CPF são obrigatórios.");
            pausar();
            return null;
        }

        if(conjuge != null){
            clienteCadastro = new Cliente(nome.trim(), cpf.trim(), rg.trim(), estado, conjuge);
        } else {
            clienteCadastro = new Cliente(nome.trim(), cpf.trim(), rg.trim(), estado);
        }

        if (service.adicionarCliente(clienteCadastro)) {
            System.out.println("Cadastro bem sucedido");
            return clienteCadastro;
        }
        System.out.println("[ERRO] Falha no cadastro (nome/CPF obrigatórios, CPF já existente ou erro ao salvar).");
        return null;
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

        if (!Validar.textoNaoVazio(nome) || !Validar.textoNaoVazio(cpf)) {
            System.out.println("[ERRO] Nome e CPF do cônjuge são obrigatórios.");
            return null;
        }
        return new Conjuge(nome.trim(), cpf.trim(), rg.trim());
    }

    private void buscarCliente(){
        System.out.println("\n========================================");
        System.out.println("           BUSCAR CLIENTE");
        System.out.println("========================================");
        System.out.print("Digite o NOME ou CPF do cliente: ");
        String busca = scan.nextLine();

        if (Validar.normalizarCpf(busca).isEmpty() && service.contarClientesPorNome(busca) > 1) {
            System.out.println("[ERRO] Existem vários clientes com esse nome. Busque pelo CPF.");
            pausar();
            return;
        }

        Cliente cliente = service.buscaCliente(busca);

        if (cliente == null) {
            System.out.println("Cliente não encontrado!!! Insira os dados novamente ou cadastre um novo cliente");
            pausar();
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

        if (Validar.normalizarCpf(cD).isEmpty() && service.contarClientesPorNome(cD) > 1) {
            System.out.println("\n[ERRO] Existem vários clientes com esse nome. Informe o CPF.");
            pausar();
            return;
        }

        Cliente clienteMod = service.buscaCliente(cD);

        if (clienteMod == null) {
            System.out.println("\n[ERRO] Cliente não encontrado!");
            System.out.println("Certifique-se de que o nome ou CPF está correto.");
            pausar();
            return;
        }

        boolean alterado = false;
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
            op = lerInt();

            switch (op) {
                case 1:
                    System.out.print(" > Novo Nome: ");
                    clienteMod.setNome(scan.nextLine());
                    alterado = true;
                    break;
                case 2:
                    System.out.print(" > Novo CPF: ");
                    String cpfAntigo = clienteMod.getCpf();
                    String cpfNovo = scan.nextLine();
                    if (!Validar.normalizarCpf(cpfAntigo).equals(Validar.normalizarCpf(cpfNovo))) {
                        Cliente existente = service.buscaCliente(cpfNovo);
                        if (existente != null) {
                            System.out.println("[ERRO] CPF já cadastrado para outro cliente.");
                            break;
                        }
                    }
                    clienteMod.setCpf(cpfNovo);
                    service.atualizarReferenciasCpfCliente(cpfAntigo, cpfNovo);
                    alterado = true;
                    break;
                case 3:
                    System.out.print(" > Novo RG: ");
                    clienteMod.setRg(scan.nextLine());
                    alterado = true;
                    break;
                case 4:
                    menuEstadoCivil(clienteMod);
                    alterado = true;
                    break;
                case 5:
                    Conjuge novoConjuge = cadastrarConjuge();
                    if (novoConjuge != null) {
                        clienteMod.setConjugue(novoConjuge);
                        clienteMod.setEstadoCivil(EstadoCivil.CASADO);
                        alterado = true;
                    }
                    break;
            }
        } while(op != 0);

        if (alterado) {
            service.getDados().gravarArquivo();
            System.out.println("\n[OK] Informação atualizada com sucesso!");
        } else {
            System.out.println("\n[AVISO] Nenhuma alteração foi realizada.");
        }
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
        int op = lerInt();

        switch(op){
            case 1:
                cliente.setEstadoCivil(EstadoCivil.SOLTEIRO);
                cliente.setConjugue(null);
                break;
            case 2:
                Conjuge c = cadastrarConjuge();
                if (c != null) {
                    cliente.setEstadoCivil(EstadoCivil.CASADO);
                    cliente.setConjugue(c);
                }
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
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pausar(){
        System.out.println("\nPressione ENTER para continuar...");
        scan.nextLine();
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

    private double lerDouble() {
        while (true) {
            String linha = scan.nextLine().trim().replace(",", ".");
            if (linha.isEmpty()) {
                System.out.println("[ERRO] Digite um número válido.");
                continue;
            }
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Digite um número válido (use ponto ou vírgula para decimais).");
            }
        }
    }
}