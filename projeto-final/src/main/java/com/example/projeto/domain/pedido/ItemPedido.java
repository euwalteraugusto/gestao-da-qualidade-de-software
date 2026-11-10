package com.example.projeto.domain.pedido;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Item de um pedido (Value Object imutavel).
 *
 * @param preco      preco unitario (nao nulo, nao negativo)
 * @param quantidade quantidade (maior que zero)
 */
public record ItemPedido(BigDecimal preco, int quantidade) {

    public ItemPedido {
        Objects.requireNonNull(preco, "preco do item e obrigatorio");
        if (preco.signum() < 0) {
            throw new IllegalArgumentException("preco do item nao pode ser negativo");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser maior que zero");
        }
    }

    /** Move Method (Feature Envy): o proprio item sabe calcular seu subtotal. */
    public BigDecimal subtotal() {
        return preco.multiply(BigDecimal.valueOf(quantidade));
    }
}
