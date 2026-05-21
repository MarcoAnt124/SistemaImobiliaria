import model.Vendedor;
import repository.DadosRepository;
import service.AutenticacaoService;
import service.ImobiliariaService;
import view.MenuPrincipal;
import view.SubMenus;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DadosRepository repository = new DadosRepository();
        boolean admExiste = false;
        for (Vendedor v : repository.listaVendedores()) {
            if (v.getIdVendedor() == 666) {
                admExiste = true;
                break;
            }
        }
        if (!admExiste) {
            if (!repository.anexarVendedor(new Vendedor(666, "ADM"))) {
                System.out.println("[ERRO] Não foi possível criar o usuário administrador (666) no arquivo de dados.");
            }
        }
        AutenticacaoService autenticacaoService = new AutenticacaoService(repository);
        ImobiliariaService imobiliariaService = new ImobiliariaService(repository);
        Scanner scan = new Scanner(System.in);
        SubMenus subMenus = new SubMenus(imobiliariaService, scan, autenticacaoService);
        MenuPrincipal menuPrincipal = new MenuPrincipal(autenticacaoService, imobiliariaService, subMenus, scan );

        menuPrincipal.iniciarSistema();
        scan.close();
    }
}
