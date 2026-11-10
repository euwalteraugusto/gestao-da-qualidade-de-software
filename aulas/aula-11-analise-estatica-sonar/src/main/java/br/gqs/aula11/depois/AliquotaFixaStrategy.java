package br.gqs.aula11.depois;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Imposto com alíquota única sobre o valor. */
public class AliquotaFixaStrategy implements ImpostoStrategy {

    private final BigDecimal aliquota;

    /**
     * Cria a estratégia.
     *
     * @param aliquota alíquota decimal, por exemplo {@code "0.17"} para 17%
     */
    public AliquotaFixaStrategy(String aliquota) {
        this.aliquota = new BigDecimal(aliquota);
    }

    @Override
    public BigDecimal calcular(BigDecimal valor) {
        return valor.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }
}
