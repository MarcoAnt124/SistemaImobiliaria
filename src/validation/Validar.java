package validation;

public class Validar {

    // Implementar os métodos descritos no README.md

    public static boolean validarCPF(String cpf) {
        //CPFs são aceitos tanto incluindo pontos e hífens ("123.456.789-10") quanto apenas os dígitos ("12345678910").
        //O método deve validar ambos os formatos corretamente.

        if (cpf == null) {
            return false;
        }

        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            return false;
        }

        // Números de CPF com todos dígitos iguais são inválidos
        if (digits.matches("(\\d)\\1{10}")) {
            return false;
        }

        int[] valores = new int[11];
        for (int i = 0; i < 11; i++) {
            valores[i] = digits.charAt(i) - '0';
        }

        // Primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += valores[i] * (10 - i);
        }
        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;
        if (valores[9] != digito1) {
            return false;
        }

        // Segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += valores[i] * (11 - i);
        }
        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;
        if (valores[10] != digito2) {
            return false;
        }

        return true;
    }

    public static boolean validarDesconto(double descontoPercentual) {
        // Desconto deve estar em valores percentuais (ex: 8.0 para 8%).
        // Não aceitamos valores menores que 1 (ex: 0.8), valores negativos, ou 0%.

        // Permitir apenas valores de 1% a 10%.
        if (descontoPercentual < 1.0 || descontoPercentual > 10.0) {
            return false;
        }
        return true;
    }

    public static String formatarValorReais(double valor) {
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,##0.00", symbols);
        return "R$ " + format.format(valor);
    }
}


