package br.gqs.aula03.depois;

/**
 * Resultado imutável do processamento de um pagamento.
 *
 * @param tipo tipo do pagamento (por exemplo {@code "CREDITO"})
 * @param valorOriginal valor informado pelo cliente
 * @param valorTotal valor final, com taxas
 * @param parcelas número de parcelas
 * @param comprovante texto do comprovante
 */
public record ResultadoPagamento(
    String tipo, double valorOriginal, double valorTotal, int parcelas, String comprovante) {
}
