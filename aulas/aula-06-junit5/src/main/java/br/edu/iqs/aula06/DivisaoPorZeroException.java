package br.edu.iqs.aula06;

/**
 * Lancada quando se tenta dividir por zero.
 * Estende {@link ArithmeticException} (unchecked), a mesma familia do erro nativo do Java.
 */
public class DivisaoPorZeroException extends ArithmeticException {

    public DivisaoPorZeroException() {
        super("Divisão por zero não permitida");
    }
}
