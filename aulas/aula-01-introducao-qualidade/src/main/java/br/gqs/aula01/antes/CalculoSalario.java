package br.gqs.aula01.antes;

/**
 * Versao "ANTES" (codigo ruim), mantida de proposito como material de estudo.
 *
 * <p>Problemas: nomes obscuros ({@code s}, {@code d}, {@code sl}, {@code r}),
 * numeros magicos, metodo pouco legivel e nenhuma validacao (um salario
 * negativo e aceito e gera "salario liquido" maior que o bruto).
 */
public class CalculoSalario {

    public static void main(String[] args) {
        double s = 5000;
        double d = calcular(s);
        System.out.println("Salário líquido: " + d);
    }

    public static double calcular(double sl) {
        double r = 0;
        if (sl < 1000) r = sl * 0.05;
        else if (sl < 2000) r = sl * 0.1;
        else if (sl < 4000) r = sl * 0.15;
        else r = sl * 0.2;
        return sl - r;
    }
}
