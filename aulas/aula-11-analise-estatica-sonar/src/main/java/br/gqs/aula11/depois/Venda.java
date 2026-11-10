package br.gqs.aula11.depois;

import java.math.BigDecimal;

/**
 * Venda imutável com valor monetário em BigDecimal.
 *
 * @param cliente nome do cliente
 * @param valor valor da venda
 */
public record Venda(String cliente, BigDecimal valor) {
}
