package br.gqs.aula03.depois;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.gqs.aula03.antes.Relatorio;
import br.gqs.aula03.modelo.Cliente;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class RelatorioClientesTest {

  private final RelatorioClientes relatorio = new RelatorioClientes();

  @Test
  void retornaApenasMaioresDeIdadeNaOrdemOriginal() {
    List<Cliente> clientes = List.of(new Cliente("Ana", 30), new Cliente("Bia", 10),
        new Cliente("Caio", 19));
    assertEquals(List.of("Ana", "Caio"), relatorio.nomesDeMaioresDeIdade(clientes));
  }

  @Test
  void quemTemExatamente18AnosEIncluido() {
    List<Cliente> clientes = List.of(new Cliente("Duda", 18), new Cliente("Edu", 17));
    assertEquals(List.of("Duda"), relatorio.nomesDeMaioresDeIdade(clientes));
  }

  @Test
  void listaNulaOuVaziaResultaEmListaVazia() {
    assertTrue(relatorio.nomesDeMaioresDeIdade(null).isEmpty());
    assertTrue(relatorio.nomesDeMaioresDeIdade(List.of()).isEmpty());
  }

  @Test
  void elementosNulosSaoIgnorados() {
    List<Cliente> clientes = new ArrayList<>(Arrays.asList(null, new Cliente("Fabi", 40)));
    assertEquals(List.of("Fabi"), relatorio.nomesDeMaioresDeIdade(clientes));
  }

  @Test
  void imprimeUmNomePorLinhaNaSaidaInformada() {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    List<Cliente> clientes = List.of(new Cliente("Ana", 30), new Cliente("Caio", 19));
    relatorio.imprimirClientesMaioresDeIdade(clientes, new PrintStream(buffer, true));
    assertEquals("Ana" + System.lineSeparator() + "Caio" + System.lineSeparator(),
        buffer.toString());
  }

  @Test
  void imprimirNaSaidaPadraoNaoLancaExcecao() {
    relatorio.imprimirClientesMaioresDeIdade(List.of(new Cliente("Gabi", 50)));
  }

  @Test
  void mesmaSaidaDaVersaoAntesExcetoNaFronteiraDos18Anos() {
    List<Cliente> clientes = List.of(new Cliente("Ana", 30), new Cliente("Bia", 10),
        new Cliente("Caio", 19), new Cliente("Duda", 18));
    ByteArrayOutputStream bufferAntes = new ByteArrayOutputStream();
    PrintStream original = System.out;
    System.setOut(new PrintStream(bufferAntes, true));
    try {
      new Relatorio().gerar(clientes);
    } finally {
      System.setOut(original);
    }
    List<String> saidaAntes = Arrays.asList(bufferAntes.toString().split(System.lineSeparator()));
    List<String> saidaDepois = new ArrayList<>(relatorio.nomesDeMaioresDeIdade(clientes));
    saidaDepois.remove("Duda");
    assertEquals(saidaAntes, saidaDepois);
  }

  @Test
  void clienteRejeitaDadosInvalidos() {
    assertThrows(IllegalArgumentException.class, () -> new Cliente(null, 20));
    assertThrows(IllegalArgumentException.class, () -> new Cliente("  ", 20));
    assertThrows(IllegalArgumentException.class, () -> new Cliente("Zé", -1));
  }

  @Test
  void clienteExpoeNomeEIdade() {
    Cliente cliente = new Cliente("Hugo", 22);
    assertEquals("Hugo", cliente.getNome());
    assertEquals(22, cliente.getIdade());
  }
}
