package com.example.projeto.domain.pedido;

import com.example.projeto.domain.pedido.desconto.DescontoPremium;
import com.example.projeto.domain.pedido.desconto.DescontoVIP;
import com.example.projeto.domain.pedido.desconto.PoliticaDescontoFactory;
import com.example.projeto.domain.pedido.desconto.SemDesconto;
import com.example.projeto.domain.pedido.frete.CalculadoraFrete;
import com.example.projeto.domain.pedido.frete.FreteFixo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoServiceTest {

    private final PoliticaDescontoFactory factory =
            new PoliticaDescontoFactory(List.of(new DescontoVIP(), new DescontoPremium(), new SemDesconto()));

    /** Equivalente ao teste do slide: estrategia VIP real + frete mockado (Arrange/Act/Assert). */
    @Test
    void deveCalcularTotalComDescontoVIP() {
        CalculadoraFrete freteMock = mock(CalculadoraFrete.class);
        when(freteMock.calcular(any())).thenReturn(BigDecimal.ZERO);

        PedidoService service = new PedidoService(factory, freteMock);
        Pedido pedido = new Pedido(List.of(new ItemPedido(new BigDecimal("100"), 1)));

        ResumoPedido resumo = service.calcularTotal(pedido, TipoCliente.VIP);

        assertEquals(new BigDecimal("90.00"), resumo.total());
        assertEquals(new BigDecimal("10.00"), resumo.desconto());
        verify(freteMock).calcular(new BigDecimal("90.00"));
    }

    /** Mesmos cenarios do PedidoService legado (comportamento preservado pela refatoracao). */
    @ParameterizedTest(name = "{0}: subtotal {1} -> total {2}")
    @CsvSource({
            "REGULAR, 50.00,  65.00",   // sem desconto, abaixo de 100 -> +15 de frete
            "REGULAR, 200.00, 200.00",  // sem desconto, frete gratis
            "VIP,     200.00, 180.00",  // 10% off
            "PREMIUM, 200.00, 160.00",  // 20% off
            "PREMIUM, 110.00, 103.00"   // 20% off -> 88.00 (< 100) + 15 de frete
    })
    void deveAplicarDescontoEFreteConformeTipoDeCliente(TipoCliente tipo, String subtotal, String esperado) {
        PedidoService service = new PedidoService(factory,
                new FreteFixo(new BigDecimal("100"), new BigDecimal("15.00")));
        Pedido pedido = new Pedido(List.of(new ItemPedido(new BigDecimal(subtotal), 1)));

        assertEquals(0, new BigDecimal(esperado).compareTo(service.calcularTotal(pedido, tipo).total()));
    }

    @Test
    void deveSomarItensComQuantidade() {
        Pedido pedido = new Pedido(List.of(
                new ItemPedido(new BigDecimal("10.50"), 2),
                new ItemPedido(new BigDecimal("4.00"), 3)));
        assertEquals(new BigDecimal("33.00"), pedido.subtotal());
    }

    @Test
    void tipoClienteNuloUsaPoliticaRegular() {
        assertEquals(TipoCliente.REGULAR, factory.obter(null).tipoCliente());
    }

    @Test
    void deveRejeitarPedidoSemItensEItensInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(BigDecimal.ONE, 0));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(new BigDecimal("-1"), 1));
        assertThrows(NullPointerException.class, () -> new ItemPedido(null, 1));
    }
}
