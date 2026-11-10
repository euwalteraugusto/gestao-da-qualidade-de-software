package br.gqs.aula03.depois;

/**
 * Estratégia de processamento de um tipo de pagamento (Strategy + OCP).
 *
 * <p>Para suportar um novo tipo de pagamento basta criar outra implementação e
 * registrá-la no {@link PagamentoService}, sem alterar o código existente.
 */
public interface PagamentoStrategy {

  /**
   * Identifica o tipo de pagamento tratado por esta estratégia.
   *
   * @return nome do tipo, por exemplo {@code "CREDITO"}
   */
  String tipo();

  /**
   * Processa o pagamento.
   *
   * @param valor valor original, já validado como positivo
   * @return resultado com total e comprovante
   */
  ResultadoPagamento processar(double valor);
}
