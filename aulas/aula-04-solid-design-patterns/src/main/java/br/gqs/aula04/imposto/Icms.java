package br.gqs.aula04.imposto;

/** ICMS: alíquota de 10% (valor didático do slide, não é a alíquota real). */
public final class Icms implements ImpostoStrategy {

    static final double ALIQUOTA = 0.10;

    @Override
    public double calcular(double valor) {
        return valor * ALIQUOTA;
    }
}
