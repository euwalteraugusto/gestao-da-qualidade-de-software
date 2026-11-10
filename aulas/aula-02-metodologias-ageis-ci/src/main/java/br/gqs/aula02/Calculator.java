package br.gqs.aula02;

/**
 * Classe utilitária para operações matemáticas básicas.
 * Fornece métodos estáticos para cálculos simples.
 *
 * <p>Correção em relação ao slide: o Javadoc original prometia
 * {@link ArithmeticException} em caso de overflow, mas {@code a + b} nunca lança
 * exceção (estoura silenciosamente). Aqui usamos {@link Math#addExact(int, int)} e
 * {@link Math#subtractExact(int, int)}, que cumprem o contrato documentado.
 */
public final class Calculator {

    private Calculator() {
    }

    /**
     * Realiza a adição de dois números inteiros.
     *
     * @param a primeiro operando (número a ser somado)
     * @param b segundo operando (número a ser somado)
     * @return resultado da soma {@code a + b}
     * @throws ArithmeticException se ocorrer overflow na operação
     */
    public static int add(int a, int b) {
        return Math.addExact(a, b);
    }

    /**
     * Realiza a subtração de dois números inteiros (desafio da aula).
     *
     * @param a minuendo (número do qual se subtrai)
     * @param b subtraendo (número a subtrair)
     * @return resultado da subtração {@code a - b}
     * @throws ArithmeticException se ocorrer overflow na operação
     */
    public static int subtract(int a, int b) {
        return Math.subtractExact(a, b);
    }
}
