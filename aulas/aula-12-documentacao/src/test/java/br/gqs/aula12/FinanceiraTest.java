package br.gqs.aula12;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FinanceiraTest {

    private final Financeira financeira = new Financeira();

    @Test
    void jurosCompostosSegueOExemploDaDocumentacao() {
        assertEquals(1210.0, financeira.jurosCompostos(1000.0, 0.1, 2), 1e-9);
    }

    @Test
    void jurosSimplesSegueOExemploDaDocumentacao() {
        assertEquals(1200.0, financeira.jurosSimples(1000.0, 0.1, 2), 1e-9);
    }

    @Test
    void zeroMesesDevolveOPrincipal() {
        assertEquals(500.0, financeira.jurosCompostos(500.0, 0.2, 0), 0.0);
    }

    @Test
    void parametrosNegativosLancamExcecaoDocumentada() {
        assertThrows(IllegalArgumentException.class, () -> financeira.jurosCompostos(1, -0.1, 1));
        assertThrows(IllegalArgumentException.class, () -> financeira.jurosCompostos(1, 0.1, -1));
        assertThrows(IllegalArgumentException.class, () -> financeira.jurosSimples(1, -0.1, 1));
    }
}
