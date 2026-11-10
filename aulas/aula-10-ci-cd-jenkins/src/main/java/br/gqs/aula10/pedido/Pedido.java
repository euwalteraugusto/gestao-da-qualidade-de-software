package br.gqs.aula10.pedido;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pedido fechado, com total já calculado.
 *
 * @param id identificador do pedido
 * @param itens itens do pedido (lista imutável)
 * @param total valor final após o desconto
 */
public record Pedido(String id, List<ItemPedido> itens, BigDecimal total) {

    /** Garante imutabilidade da lista de itens. */
    public Pedido {
        itens = List.copyOf(itens);
    }
}
