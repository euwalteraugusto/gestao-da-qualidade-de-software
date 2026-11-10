package br.gqs.aula04.imposto;

/** ISS: alíquota de 5% (valor didático do slide, não é a alíquota real). */
public final class Iss implements ImpostoStrategy {

    static final double ALIQUOTA = 0.05;

    @Override
    public double calcular(double valor) {
        return valor * ALIQUOTA;
    }
}
