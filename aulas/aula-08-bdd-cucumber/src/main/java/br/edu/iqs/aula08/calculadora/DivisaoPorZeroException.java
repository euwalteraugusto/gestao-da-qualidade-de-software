package br.edu.iqs.aula08.calculadora;

/** Lancada ao dividir por zero (mensagem exigida pela feature de calculadora). */
public class DivisaoPorZeroException extends ArithmeticException {

    public DivisaoPorZeroException() {
        super("Divisão por zero não permitida");
    }
}
