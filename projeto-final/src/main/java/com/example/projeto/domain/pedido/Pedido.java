package com.example.projeto.domain.pedido;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pedido com seus itens (Value Object imutavel, usado no calculo do total).
 */
public record Pedido(List<ItemPedido> itens) {

    public Pedido {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("pedido deve ter ao menos um item");
        }
        itens = List.copyOf(itens);
    }

    public BigDecimal subtotal() {
        return itens.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
