package br.gqs.aula03.depois;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gqs.aula03.antes.VersaoAntesTestSupport;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PagamentoServiceTest {

  private final PagamentoService service = PagamentoService.padrao();

  @Test
  void creditoAteTrezentosFicaEmUmaParcela() {
    ResultadoPagamento resultado = service.processar("CREDITO", 100);
    assertEquals(1, resultado.parcelas());
    assertEquals(102.99, resultado.valorTotal(), 1e-9);
    assertEquals("CREDITO: total=102.99 em 1x de 102.99", resultado.comprovante());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1, 100, 291.0, 292.0, 300, 500, 970, 971, 5000})
  void creditoParcelaConformeOTotal(double valor) {
    ResultadoPagamento resultado = service.processar("CREDITO", valor);
    double total = valor + valor * PagamentoCredito.TAXA;
    int esperado = total > 1000 ? 6 : total > 300 ? 3 : 1;
    assertEquals(esperado, resultado.parcelas());
    assertEquals(total, resultado.valorTotal(), 1e-9);
  }

  @Test
  void boletoSomaTarifaFixa() {
    ResultadoPagamento resultado = service.processar("BOLETO", 100);
    assertEquals(103.5, resultado.valorTotal(), 1e-9);
    assertEquals(1, resultado.parcelas());
    assertEquals("BOLETO: total=103.50 vencimento em 3 dias", resultado.comprovante());
  }

  @Test
  void tipoNaoDiferenciaMaiusculasEEspacos() {
    assertEquals("BOLETO", service.processar("  boleto ", 10).tipo());
  }

  @ParameterizedTest
  @ValueSource(doubles = {0, -1, Double.NaN, Double.POSITIVE_INFINITY})
  void rejeitaValorInvalido(double valor) {
    assertThrows(IllegalArgumentException.class, () -> service.processar("CREDITO", valor));
  }

  @Test
  void rejeitaTipoNuloOuDesconhecido() {
    assertThrows(IllegalArgumentException.class, () -> service.processar(null, 10));
    assertThrows(IllegalArgumentException.class, () -> service.processar("PIX", 10));
  }

  @Test
  void comprovanteIgualAoQueAVersaoAntesImprime() {
    for (String tipo : List.of("CREDITO", "BOLETO")) {
      for (double valor : new double[] {10, 299, 300, 301, 990, 1000, 2500.55}) {
        String impresso = VersaoAntesTestSupport.imprimir(tipo, valor);
        assertEquals(impresso, service.processar(tipo, valor).comprovante() + System.lineSeparator(),
            tipo + " " + valor);
      }
    }
  }

  /** Demonstra o OCP: novo tipo de pagamento sem alterar PagamentoService nem as demais classes. */
  @Test
  void novoTipoPodeSerAdicionadoSemModificarOServico() {
    PagamentoStrategy pix = new PagamentoStrategy() {
      @Override
      public String tipo() {
        return "PIX";
      }

      @Override
      public ResultadoPagamento processar(double valor) {
        return new ResultadoPagamento("PIX", valor, valor, 1, "PIX: total=" + valor);
      }
    };
    PagamentoService comPix = new PagamentoService(
        List.of(new PagamentoCredito(), new PagamentoBoleto(), pix));
    assertEquals("PIX: total=50.0", comPix.processar("pix", 50).comprovante());
  }

  @Test
  void rejeitaTiposRepetidosEListaNula() {
    List<PagamentoStrategy> repetidas = List.of(new PagamentoBoleto(), new PagamentoBoleto());
    assertThrows(IllegalArgumentException.class, () -> new PagamentoService(repetidas));
    assertThrows(NullPointerException.class, () -> new PagamentoService(null));
  }
}
