package br.gqs.aula01.antes;

/**
 * Versao "ANTES" do exercicio dos alunos (codigo ruim, mantido como material).
 *
 * <p>Problemas: nome {@code calcTotal} vago, variavel {@code t}, numeros magicos
 * ({@code 1000} e {@code 0.9}), sem validacao de nulos nem de tamanhos diferentes
 * (arrays de tamanhos distintos geram {@code ArrayIndexOutOfBoundsException}
 * ou ignoram itens silenciosamente).
 */
public class OrderProcessor {

    public double calcTotal(double[] prices, int qty[]) {
        double t = 0;
        for (int i = 0; i < prices.length; i++) {
            t += prices[i] * qty[i];
        }
        if (t > 1000) {
            t = t * 0.9;
        }
        return t;
    }
}
