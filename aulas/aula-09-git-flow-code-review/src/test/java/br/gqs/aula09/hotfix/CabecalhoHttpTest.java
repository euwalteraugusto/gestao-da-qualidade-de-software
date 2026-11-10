package br.gqs.aula09.hotfix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CabecalhoHttpTest {

    private final CabecalhoHttp cabecalho = new CabecalhoHttp();

    @Test
    void devolveValorSemEspacos() {
        Map<String, String> cabecalhos = Map.of("Content-Type", " application/json ");
        assertEquals("application/json", cabecalho.obterValor(cabecalhos, "Content-Type").orElseThrow());
    }

    @Test
    void cabecalhoAusenteNaoLancaNullPointerException() {
        assertTrue(cabecalho.obterValor(Map.of(), "X-Ausente").isEmpty());
    }

    @Test
    void cabecalhoComValorNuloEhTratadoComoAusente() {
        Map<String, String> cabecalhos = new HashMap<>();
        cabecalhos.put("X-Nulo", null);
        assertTrue(cabecalho.obterValor(cabecalhos, "X-Nulo").isEmpty());
    }
}
