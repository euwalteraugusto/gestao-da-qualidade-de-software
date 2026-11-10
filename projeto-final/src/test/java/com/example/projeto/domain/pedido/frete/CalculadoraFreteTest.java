package com.example.projeto.domain.pedido.frete;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CalculadoraFreteTest {

    private final FreteFixo freteFixo = new FreteFixo(new BigDecimal("100"), new BigDecimal("15.00"));

    @Test
    void freteFixoAbaixoDoMinimo() {
        assertThat(freteFixo.calcular(new BigDecimal("99.99"))).isEqualByComparingTo("15.00");
    }

    @Test
    void freteGratisAPartirDoMinimo() {
        assertThat(freteFixo.calcular(new BigDecimal("100.00"))).isEqualByComparingTo("0");
    }

    @Test
    void decoratorDelegaParaACalculadoraDecorada() {
        CalculadoraFrete interna = mock(CalculadoraFrete.class);
        when(interna.calcular(new BigDecimal("50"))).thenReturn(new BigDecimal("7.00"));

        CalculadoraFrete comLog = new CalculadoraFreteComLog(interna);

        assertThat(comLog.calcular(new BigDecimal("50"))).isEqualByComparingTo("7.00");
        verify(interna).calcular(new BigDecimal("50"));
    }
}
