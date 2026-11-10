package br.gqs.aula10.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Regra de desconto progressivo por faixa de valor. */
public class CalculadoraDesconto {

    private static final BigDecimal FAIXA_ALTA = new BigDecimal("1000");
    private static final BigDecimal FAIXA_MEDIA = new BigDecimal("500");
    private static final BigDecimal DESCONTO_ALTO = new BigDecimal("0.10");
    private static final BigDecimal DESCONTO_MEDIO = new BigDecimal("0.05");

    /**
     * Calcula o valor do desconto.
     *
     * <p>Subtotal a partir de 1000: 10%; a partir de 500: 5%; abaixo disso: sem desconto.</p>
     *
     * @param subtotal valor bruto do pedido (não negativo)
     * @return valor do desconto, com duas casas decimais
     * @throws IllegalArgumentException se o subtotal for nulo ou negativo
     */
    public BigDecimal calcular(BigDecimal subtotal) {
        if (subtotal == null || subtotal.signum() < 0) {
            throw new IllegalArgumentException("Subtotal inválido");
        }
        BigDecimal percentual = BigDecimal.ZERO;
        if (subtotal.compareTo(FAIXA_ALTA) >= 0) {
            percentual = DESCONTO_ALTO;
        } else if (subtotal.compareTo(FAIXA_MEDIA) >= 0) {
            percentual = DESCONTO_MEDIO;
        }
        return subtotal.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }
}
