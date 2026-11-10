package br.gqs.aula01.depois;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OrderProcessorTest {

    private static final double DELTA = 1e-9;
    private final OrderProcessor processor = new OrderProcessor();
    private final br.gqs.aula01.antes.OrderProcessor antigo = new br.gqs.aula01.antes.OrderProcessor();

    @Test
    void pedidoVazioTemTotalZero() {
        assertEquals(0.0, processor.calculateTotal(new double[0], new int[0]), DELTA);
    }

    @Test
    void semDescontoQuandoSubtotalIgualAoLimite() {
        // fronteira: 1000 exatos NAO recebem desconto (regra "> 1000")
        assertEquals(1000.0, processor.calculateTotal(new double[] {500, 250}, new int[] {1, 2}), DELTA);
    }

    @Test
    void semDescontoAbaixoDoLimite() {
        assertEquals(999.99, processor.calculateTotal(new double[] {999.99}, new int[] {1}), DELTA);
    }

    @Test
    void descontoDe10PorCentoAcimaDoLimite() {
        assertEquals(1350.0, processor.calculateTotal(new double[] {500, 250}, new int[] {2, 2}), DELTA);
    }

    @Test
    void descontoNaFronteiraSuperior() {
        assertEquals(1000.01 * 0.9, processor.calculateTotal(new double[] {1000.01}, new int[] {1}), 1e-6);
    }

    @Test
    void equivaleAVersaoRuimParaEntradasValidas() {
        double[][] precos = {{10, 20, 30}, {500, 250}, {1000.01}, {0, 0}, {99.9, 0.1}};
        int[][] qtds = {{1, 2, 3}, {2, 2}, {1}, {5, 5}, {10, 10}};
        for (int i = 0; i < precos.length; i++) {
            assertEquals(antigo.calcTotal(precos[i], qtds[i]),
                    processor.calculateTotal(precos[i], qtds[i]), 1e-6);
        }
    }

    @Test
    void rejeitaArraysNulos() {
        assertThrows(IllegalArgumentException.class, () -> processor.calculateTotal(null, new int[] {1}));
        assertThrows(IllegalArgumentException.class, () -> processor.calculateTotal(new double[] {1}, null));
    }

    @Test
    void rejeitaTamanhosDiferentes() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> processor.calculateTotal(new double[] {10, 20}, new int[] {1}));
        assertEquals("Prices and quantities arrays must have the same length.", ex.getMessage());
    }

    @Test
    void rejeitaPrecoNegativoENaN() {
        assertThrows(IllegalArgumentException.class, () -> processor.calculateTotal(new double[] {-1}, new int[] {1}));
        assertThrows(IllegalArgumentException.class,
                () -> processor.calculateTotal(new double[] {Double.NaN}, new int[] {1}));
    }

    @Test
    void rejeitaQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class, () -> processor.calculateTotal(new double[] {1}, new int[] {-1}));
    }
}
