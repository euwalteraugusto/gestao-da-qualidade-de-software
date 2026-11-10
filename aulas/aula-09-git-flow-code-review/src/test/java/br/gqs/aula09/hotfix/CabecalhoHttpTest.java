package br.gqs.aula09.hotfix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CabecalhoHttpTest {

    private final CabecalhoHttp cabecalho = new CabecalhoHttp();

    @Test
    void devolveValorSemEspacos() {
        Map<String, String> cabecalhos = Map.of("Content-Type", " application/json ");
        assertEquals("application/json", cabecalho.obterValor(cabecalhos, "Content-Type"));
    }
}
