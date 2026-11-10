package br.gqs.aula10.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PedidoUnitTest {

    private final CalculadoraDesconto calculadora = new CalculadoraDesconto();

    @ParameterizedTest
    @CsvSource({"0,0.00", "499.99,0.00", "500,25.00", "999.99,50.00", "1000,100.00", "2500,250.00"})
    void descontoPorFaixa(String subtotal, String esperado) {
        assertEquals(new BigDecimal(esperado), calculadora.calcular(new BigDecimal(subtotal)));
    }

    @Test
    void descontoRejeitaSubtotalNuloOuNegativo() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(null));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(new BigDecimal("-1")));
    }

    @Test
    void itemCalculaSubtotal() {
        assertEquals(new BigDecimal("30.00"), new ItemPedido("A", 3, new BigDecimal("10.00")).subtotal());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void itemRejeitaSkuInvalido(String sku) {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(sku, 1, BigDecimal.ONE));
    }

    @Test
    void itemRejeitaQuantidadeEPrecoInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A", 0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A", 1, null));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido("A", 1, new BigDecimal("-0.01")));
    }

    @Test
    void itemAceitaPrecoZero() {
        assertEquals(BigDecimal.ZERO, new ItemPedido("BRINDE", 1, BigDecimal.ZERO).subtotal());
    }

    @Test
    void pedidoTemListaImutavel() {
        List<ItemPedido> origem = new ArrayList<>(List.of(new ItemPedido("A", 1, BigDecimal.TEN)));
        Pedido pedido = new Pedido("P", origem, BigDecimal.TEN);
        origem.clear();
        assertEquals(1, pedido.itens().size());
        assertThrows(UnsupportedOperationException.class, () -> pedido.itens().clear());
    }

    @Test
    void fecharPedidoAplicaDescontoESalva() {
        RepositorioMemoria repositorio = new RepositorioMemoria();
        PedidoService service = new PedidoService(calculadora, repositorio);
        Pedido pedido = service.fechar("P1", List.of(new ItemPedido("A", 2, new BigDecimal("300.00"))));
        assertEquals(new BigDecimal("570.00"), pedido.total());
        assertTrue(repositorio.buscar("P1").isPresent());
    }

    @Test
    void fecharPedidoValidaEntradas() {
        PedidoService service = new PedidoService(calculadora, new RepositorioMemoria());
        List<ItemPedido> itens = List.of(new ItemPedido("A", 1, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> service.fechar(null, itens));
        assertThrows(IllegalArgumentException.class, () -> service.fechar(" ", itens));
        assertThrows(IllegalArgumentException.class, () -> service.fechar("P", null));
        assertThrows(IllegalArgumentException.class, () -> service.fechar("P", List.of()));
    }

    @Test
    void repositorioDevolveVazioQuandoNaoExiste() {
        assertTrue(new RepositorioMemoria().buscar("inexistente").isEmpty());
    }
}
