package br.gqs.aula03.antes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gqs.aula03.modelo.Cliente;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Testes de caracterização do código "antes" (documentam inclusive as falhas). */
class VersaoAntesTest {

  /** Executa o trecho capturando o que foi escrito em System.out. */
  static String capturarSaida(Runnable acao) {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(buffer, true));
    try {
      acao.run();
    } finally {
      System.setOut(original);
    }
    return buffer.toString();
  }

  @Test
  void relatorioImprimeSomenteMaioresQue18() {
    List<Cliente> clientes = List.of(new Cliente("Ana", 30), new Cliente("Bia", 10),
        new Cliente("Caio", 19));
    String saida = capturarSaida(() -> new Relatorio().gerar(clientes));
    assertEquals("Ana" + System.lineSeparator() + "Caio" + System.lineSeparator(), saida);
  }

  @Test
  void relatorioIgnoraQuemTemExatamente18_falhaConhecida() {
    String saida = capturarSaida(() -> new Relatorio().gerar(List.of(new Cliente("Duda", 18))));
    assertEquals("", saida);
  }

  @Test
  void relatorioComListaNulaNaoImprimeNada() {
    assertEquals("", capturarSaida(() -> new Relatorio().gerar(null)));
  }

  @Test
  void calcMultiplicaOsParametros() {
    assertEquals(12, new Geometria().calc(3, 4));
  }

  @Test
  void pagamentoCreditoAteTrezentosEmUmaParcela() {
    String saida = capturarSaida(() -> new Pagamento().processar("CREDITO", 100));
    assertEquals("CREDITO: total=102.99 em 1x de 102.99" + System.lineSeparator(), saida);
  }

  @Test
  void pagamentoBoleto() {
    String saida = capturarSaida(() -> new Pagamento().processar("BOLETO", 100));
    assertEquals("BOLETO: total=103.50 vencimento em 3 dias" + System.lineSeparator(), saida);
  }

  @Test
  void pagamentoTipoDesconhecidoEIgnoradoEmSilencio_falhaConhecida() {
    assertEquals("", capturarSaida(() -> new Pagamento().processar("PIX", 100)));
  }

  @Test
  void pagamentoTipoNuloLancaNullPointer_falhaConhecida() {
    assertThrows(NullPointerException.class, () -> new Pagamento().processar(null, 100));
  }

  @Test
  void pagamentoValorInvalidoLancaExcecao() {
    assertThrows(IllegalArgumentException.class, () -> new Pagamento().processar("BOLETO", 0));
    assertThrows(IllegalArgumentException.class, () -> new Pagamento().processar("CREDITO", -5));
  }
}
