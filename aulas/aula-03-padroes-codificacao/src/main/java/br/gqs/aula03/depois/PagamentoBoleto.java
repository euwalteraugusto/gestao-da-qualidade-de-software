package br.gqs.aula03.depois;

import java.util.Locale;

/** Pagamento por boleto: tarifa fixa de R$ 3,50 e vencimento em 3 dias. */
public final class PagamentoBoleto implements PagamentoStrategy {

  /** Identificador do tipo. */
  public static final String TIPO = "BOLETO";

  static final double TARIFA = 3.5;
  static final int PRAZO_DIAS = 3;

  @Override
  public String tipo() {
    return TIPO;
  }

  @Override
  public ResultadoPagamento processar(double valor) {
    double total = valor + TARIFA;
    String comprovante = String.format(Locale.US, "BOLETO: total=%.2f vencimento em %d dias",
        total, PRAZO_DIAS);
    return new ResultadoPagamento(TIPO, valor, total, 1, comprovante);
  }
}
