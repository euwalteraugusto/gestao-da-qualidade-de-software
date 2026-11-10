package br.gqs.aula03.antes;

/** Apoio de teste: executa a classe {@code Pagamento} (pacote privado) e captura a saída. */
public final class VersaoAntesTestSupport {

  private VersaoAntesTestSupport() {
  }

  /**
   * Executa {@code Pagamento.processar} da versão "antes" e devolve o que foi impresso.
   *
   * @param tipo tipo do pagamento
   * @param valor valor do pagamento
   * @return texto impresso em {@code System.out}
   */
  public static String imprimir(String tipo, double valor) {
    return VersaoAntesTest.capturarSaida(() -> new Pagamento().processar(tipo, valor));
  }
}
