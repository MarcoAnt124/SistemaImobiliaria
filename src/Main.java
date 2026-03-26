import repository.DadosRepository;
import service.AutenticacaoService;
import service.ImobiliariaService;
import validation.Validar;
import view.MenuPrincipal;
import view.SubMenus;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DadosRepository repository = new DadosRepository();
        AutenticacaoService autenticacaoService = new AutenticacaoService(repository);
        ImobiliariaService imobiliariaService = new ImobiliariaService(repository, autenticacaoService);
        Validar validar = new Validar();
        Scanner scan = new Scanner(System.in);
        SubMenus subMenus = new SubMenus(imobiliariaService, validar, scan, autenticacaoService);
        MenuPrincipal menuPrincipal = new MenuPrincipal(autenticacaoService, imobiliariaService, subMenus, scan );

        menuPrincipal.iniciarSistema();
        scan.close();
    }
}
