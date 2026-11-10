package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PoliticaDescontoFactoryTest {

    @Test
    void deveRetornarEstrategiaDeCadaTipo() {
        PoliticaDescontoFactory factory =
                new PoliticaDescontoFactory(List.of(new DescontoVIP(), new DescontoPremium(), new SemDesconto()));

        assertThat(factory.obter(TipoCliente.VIP)).isInstanceOf(DescontoVIP.class);
        assertThat(factory.obter(TipoCliente.PREMIUM)).isInstanceOf(DescontoPremium.class);
        assertThat(factory.obter(TipoCliente.REGULAR)).isInstanceOf(SemDesconto.class);
        assertThat(factory.obter(TipoCliente.VIP).aplicarDesconto(new BigDecimal("33.33")))
                .isEqualByComparingTo("30.00");
    }

    @Test
    void deveFalharNaSubidaSeFaltarEstrategia() {
        List<PoliticaDesconto> incompleta = List.of(new DescontoVIP(), new SemDesconto());
        assertThatThrownBy(() -> new PoliticaDescontoFactory(incompleta))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PREMIUM");
    }

    @Test
    void deveFalharSeHouverEstrategiaDuplicada() {
        List<PoliticaDesconto> duplicada = List.of(new DescontoVIP(), new DescontoVIP());
        assertThatThrownBy(() -> new PoliticaDescontoFactory(duplicada))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Mais de uma");
    }
}
