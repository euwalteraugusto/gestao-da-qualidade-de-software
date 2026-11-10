package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.pedido.TipoCliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Requisicao de calculo de total: os itens referenciam produtos cadastrados
 * (o preco vem do catalogo, nunca do cliente).
 */
public record PedidoRequest(
        @NotNull(message = "tipoCliente e obrigatorio") TipoCliente tipoCliente,
        @NotEmpty(message = "itens e obrigatorio") List<@Valid Item> itens) {

    public record Item(
            @NotNull(message = "produtoId e obrigatorio") Long produtoId,
            @Min(value = 1, message = "quantidade deve ser maior que zero") int quantidade) {
    }
}
