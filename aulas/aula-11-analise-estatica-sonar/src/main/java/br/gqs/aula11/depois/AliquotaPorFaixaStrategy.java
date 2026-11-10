package br.gqs.aula11.depois;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Imposto com duas alíquotas, conforme o valor ultrapasse (estritamente) um limite. */
public class AliquotaPorFaixaStrategy implements ImpostoStrategy {

    private final BigDecimal limite;
    private final BigDecimal aliquotaAcima;
    private final BigDecimal aliquotaAteLimite;

    /**
     * Cria a estratégia.
     *
     * @param limite valor a partir do qual (exclusive) vale a alíquota maior
     * @param aliquotaAcima alíquota para valores maiores que o limite
     * @param aliquotaAteLimite alíquota para valores até o limite (inclusive)
     */
    public AliquotaPorFaixaStrategy(String limite, String aliquotaAcima, String aliquotaAteLimite) {
        this.limite = new BigDecimal(limite);
        this.aliquotaAcima = new BigDecimal(aliquotaAcima);
        this.aliquotaAteLimite = new BigDecimal(aliquotaAteLimite);
    }

    @Override
    public BigDecimal calcular(BigDecimal valor) {
        BigDecimal aliquota = valor.compareTo(limite) > 0 ? aliquotaAcima : aliquotaAteLimite;
        return valor.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }
}
