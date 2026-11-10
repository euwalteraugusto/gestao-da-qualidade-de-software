package br.gqs.aula03.depois;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Serviço que valida a entrada e delega o processamento à estratégia do tipo.
 *
 * <p>Substitui a cadeia {@code if/else} da classe {@code Pagamento} original:
 * cada tipo é uma classe pequena e o serviço fica fechado para modificação.
 */
public class PagamentoService {

  private final Map<String, PagamentoStrategy> estrategias = new HashMap<>();

  /**
   * Cria o serviço com as estratégias informadas.
   *
   * @param estrategias estratégias disponíveis, sem tipos repetidos
   * @throws IllegalArgumentException se houver dois tipos iguais
   */
  public PagamentoService(List<PagamentoStrategy> estrategias) {
    Objects.requireNonNull(estrategias, "estrategias");
    for (PagamentoStrategy estrategia : estrategias) {
      registrar(estrategia);
    }
  }

  /**
   * Cria o serviço com as estratégias padrão (crédito e boleto).
   *
   * @return serviço pronto para uso
   */
  public static PagamentoService padrao() {
    return new PagamentoService(List.of(new PagamentoCredito(), new PagamentoBoleto()));
  }

  /**
   * Processa um pagamento do tipo informado.
   *
   * @param tipo tipo do pagamento, sem diferenciar maiúsculas de minúsculas
   * @param valor valor positivo
   * @return resultado do pagamento
   * @throws IllegalArgumentException se o tipo for nulo/desconhecido ou o valor inválido
   */
  public ResultadoPagamento processar(String tipo, double valor) {
    validarValor(valor);
    return buscarEstrategia(tipo).processar(valor);
  }

  private void registrar(PagamentoStrategy estrategia) {
    String chave = normalizar(estrategia.tipo());
    if (estrategias.putIfAbsent(chave, estrategia) != null) {
      throw new IllegalArgumentException("Tipo de pagamento repetido: " + chave);
    }
  }

  private PagamentoStrategy buscarEstrategia(String tipo) {
    if (tipo == null) {
      throw new IllegalArgumentException("Tipo de pagamento é obrigatório.");
    }
    PagamentoStrategy estrategia = estrategias.get(normalizar(tipo));
    if (estrategia == null) {
      throw new IllegalArgumentException("Tipo de pagamento desconhecido: " + tipo);
    }
    return estrategia;
  }

  private static void validarValor(double valor) {
    if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
      throw new IllegalArgumentException("Valor deve ser positivo.");
    }
  }

  private static String normalizar(String tipo) {
    return tipo.trim().toUpperCase(Locale.ROOT);
  }
}
