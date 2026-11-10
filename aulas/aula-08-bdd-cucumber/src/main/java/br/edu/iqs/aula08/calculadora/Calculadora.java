package br.edu.iqs.aula08.calculadora;

/** Calculadora usada pelas features de BDD. */
public final class Calculadora {

    private Calculadora() {
    }

    public static int somar(int a, int b) {
        return a + b;
    }

    /**
     * @return a divisao em ponto flutuante (o slide guardava em {@code int}, o que nem compilaria)
     * @throws DivisaoPorZeroException se b for zero
     */
    public static double dividir(int a, int b) {
        if (b == 0) {
            throw new DivisaoPorZeroException();
        }
        return (double) a / b;
    }
}
