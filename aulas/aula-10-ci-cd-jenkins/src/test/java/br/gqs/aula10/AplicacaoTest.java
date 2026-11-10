package br.gqs.aula10;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class AplicacaoTest {

    @Test
    void mainImprimeTotalDoPedidoDeExemplo() {
        PrintStream original = System.out;
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
        try {
            Aplicacao.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        assertTrue(saida.toString(StandardCharsets.UTF_8).contains("Total: 570.00"));
    }
}
