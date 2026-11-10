package br.gqs.aula03.depois;

import java.util.Locale;

/** Pagamento no cartão de crédito: taxa de 2,99% e parcelamento conforme o total. */
public final class PagamentoCredito implements PagamentoStrategy {

  /** Identificador do tipo. */
  public static final String TIPO = "CREDITO";

  static final double TAXA = 0.0299;
  static final double LIMITE_SEIS_PARCELAS = 1000.0;
  static final double LIMITE_TRES_PARCELAS = 300.0;
  static final int PARCELAS_MAXIMAS = 6;
  static final int PARCELAS_INTERMEDIARIAS = 3;

  @Override
  public String tipo() {
    return TIPO;
  }

  @Override
  public ResultadoPagamento processar(double valor) {
    double total = valor + valor * TAXA;
    int parcelas = definirParcelas(total);
    double valorParcela = total / parcelas;
    String comprovante = String.format(Locale.US, "CREDITO: total=%.2f em %dx de %.2f",
        total, parcelas, valorParcela);
    return new ResultadoPagamento(TIPO, valor, total, parcelas, comprovante);
  }

  private static int definirParcelas(double total) {
    if (total > LIMITE_SEIS_PARCELAS) {
      return PARCELAS_MAXIMAS;
    }
    if (total > LIMITE_TRES_PARCELAS) {
      return PARCELAS_INTERMEDIARIAS;
    }
    return 1;
  }
}
