package br.edu.iqs.aula06;

/** Calculadora do slide da Aula 6 (alvo dos primeiros testes unitarios). */
public class Calculadora {

    private Calculadora() {
    }

    /**
     * Soma dois inteiros. Atencao: nao detecta estouro; {@code Integer.MAX_VALUE + 1} "da a volta"
     * e resulta em {@code Integer.MIN_VALUE}. Use {@link #somarExato} quando o estouro for erro.
     */
    public static int somar(int a, int b) {
        return a + b;
    }

    /** Soma dois inteiros lancando {@link ArithmeticException} se houver estouro de {@code int}. */
    public static int somarExato(int a, int b) {
        return Math.addExact(a, b);
    }

    /**
     * Divide a por b em ponto flutuante (o cast preserva as casas decimais).
     *
     * @throws DivisaoPorZeroException se b for zero
     */
    public static double dividir(int a, int b) {
        if (b == 0) {
            throw new DivisaoPorZeroException();
        }
        return (double) a / b;
    }
}
