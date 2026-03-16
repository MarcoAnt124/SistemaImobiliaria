import validation.Validar;

public class MainTesteValidacao {
    public static void main(String[] args) {
        String cpfValido = "111.444.777-35";
        String cpfValido2 = "11144477735";
        String cpfInvalido = "123.456.789-00";
        String cpfInalido2 = "12345678900";
        String cpfFormatadoErrado = "1234567890";
        String cpfFormatoErrado2 = "123.456.789-0";

        System.out.println("CPF válido 1 (111.444.777-35): " + Validar.validarCPF(cpfValido));
        System.out.println("CPF válido 2 (11144477735): " + Validar.validarCPF(cpfValido2));
        System.out.println("CPF inválido 1 (123.456.789-00): " + Validar.validarCPF(cpfInvalido));
        System.out.println("CPF inválido 2 (12345678900): " + Validar.validarCPF(cpfInalido2));
        System.out.println("CPF formato errado 1 (1234567890): " + Validar.validarCPF(cpfFormatadoErrado));
        System.out.println("CPF formato errado 2 (123.456.789-0): " + Validar.validarCPF(cpfFormatoErrado2));
        System.out.println();

        double descontoValido = 5.0;
        double descontoMenor1 = 0.5;
        double descontoMaior10= 12.0;
        double descontoNegativo = -3.0;
        double descontoLimite1 = 1.0;
        double descontoLimite10 = 10.0;
        double descontoQuebrado = 7.5;

        System.out.println("Desconto válido (5%): " + Validar.validarDesconto(descontoValido));
        System.out.println("Desconto menor que 1% (0.5%): " + Validar.validarDesconto(descontoMenor1));
        System.out.println("Desconto maior que 10% (12.0%): " + Validar.validarDesconto(descontoMaior10));
        System.out.println("Desconto negativo (-3.0%): " + Validar.validarDesconto(descontoNegativo));
        System.out.println("Desconto no limite inferior (1.0%): " + Validar.validarDesconto(descontoLimite1));
        System.out.println("Desconto no limite superior (10.0%): " + Validar.validarDesconto(descontoLimite10));
        System.out.println("Desconto quebrado (7.5%): " + Validar.validarDesconto(descontoQuebrado));
        System.out.println();

        double valor = 1234.5;
        double valor2 = 524300;
        double valor3 = 0.99;
        double valor4 = 624512.54;
        System.out.println("Formatar valor 1234.5: " + Validar.formatarValorReais(valor));
        System.out.println("Formatar valor 524300: " + Validar.formatarValorReais(valor2));
        System.out.println("Formatar valor 0.99: " + Validar.formatarValorReais(valor3));
        System.out.println("Formatar valor 624512.54: " + Validar.formatarValorReais(valor4));
    }
}