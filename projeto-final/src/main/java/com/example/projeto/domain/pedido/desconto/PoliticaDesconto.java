package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;

import java.math.BigDecimal;

/**
 * <b>Strategy</b>: cada politica de desconto encapsula um algoritmo intercambiavel.
 * Substitui o {@code switch (tipoCliente)} do PedidoService legado; um novo tipo de
 * desconto e uma nova classe, sem alterar o servico (principio aberto/fechado).
 */
public interface PoliticaDesconto {

    /** Tipo de cliente atendido por esta politica (usado pela factory). */
    TipoCliente tipoCliente();

    /** Retorna o total ja com o desconto aplicado. */
    BigDecimal aplicarDesconto(BigDecimal total);
}
