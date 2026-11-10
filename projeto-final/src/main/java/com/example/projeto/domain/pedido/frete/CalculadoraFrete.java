package com.example.projeto.domain.pedido.frete;

import java.math.BigDecimal;

/**
 * Calcula o frete de um pedido a partir do total (ja com desconto).
 * Interface comum ao componente concreto ({@link FreteFixo}) e ao
 * <b>Decorator</b> ({@link CalculadoraFreteComLog}).
 */
public interface CalculadoraFrete {

    /** Retorna apenas o valor do frete para o total informado. */
    BigDecimal calcular(BigDecimal total);
}
