package validation;

public class Validar {

    public static String normalizarCpf(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }

    public static boolean validarDesconto(double descontoPercentual) {
        return descontoPercentual > 0 && descontoPercentual <= 100;
    }

    public static boolean validarValorPositivo(double valor) {
        return valor > 0;
    }

    public static boolean textoNaoVazio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static String formatarValorReais(double valor) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,##0.00", symbols);
        return "R$ " + format.format(valor);
    }
}
