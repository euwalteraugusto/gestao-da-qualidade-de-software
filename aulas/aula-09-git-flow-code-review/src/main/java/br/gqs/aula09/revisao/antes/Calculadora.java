package br.gqs.aula09.revisao.antes;

/**
 * Código problemático do PR #123, mantido apenas como registro "ANTES" da revisão.
 * NÃO usar: não trata divisão por zero (lança ArithmeticException genérica "/ by zero").
 */
public class Calculadora {

    /**
     * Divide dois inteiros sem validar o divisor.
     *
     * @param a dividendo
     * @param b divisor
     * @return a / b
     */
    public int divide(int a, int b) {
        return a / b; // Problema grave: falta tratamento de divisão por zero!
    }
}
