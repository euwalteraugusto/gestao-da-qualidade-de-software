package br.gqs.aula10.pedido;

import java.math.BigDecimal;

/**
 * Item de um pedido.
 *
 * @param sku código do produto
 * @param quantidade quantidade comprada (maior que zero)
 * @param precoUnitario preço de uma unidade (não negativo)
 */
public record ItemPedido(String sku, int quantidade, BigDecimal precoUnitario) {

    /** Valida os dados do item (fail fast). */
    public ItemPedido {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (precoUnitario == null || precoUnitario.signum() < 0) {
            throw new IllegalArgumentException("Preço unitário não pode ser negativo");
        }
    }

    /**
     * Calcula o subtotal do item.
     *
     * @return preço unitário multiplicado pela quantidade
     */
    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
