package br.gqs.aula11.depois;

import java.math.BigDecimal;

/** Estratégia de cálculo de um imposto (Strategy Pattern). */
public interface ImpostoStrategy {

    /**
     * Calcula o imposto devido sobre um valor.
     *
     * @param valor valor base (não nulo)
     * @return imposto com duas casas decimais
     */
    BigDecimal calcular(BigDecimal valor);
}
