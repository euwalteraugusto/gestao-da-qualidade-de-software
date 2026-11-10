package br.gqs.aula09;

/**
 * Calculadora usada no code review simulado (PR #123) da Aula 9.
 *
 * <p>Esta é a versão DEPOIS da revisão; a versão ANTES está em
 * {@code br.gqs.aula09.revisao.antes.Calculadora}.</p>
 */
public class Calculadora {

    /**
     * Divide dois inteiros.
     *
     * @param a dividendo
     * @param b divisor
     * @return resultado da divisão inteira
     * @throws ArithmeticException se {@code b} for zero (fail fast, com mensagem clara)
     */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Divisor não pode ser zero");
        }
        return a / b;
    }
}
