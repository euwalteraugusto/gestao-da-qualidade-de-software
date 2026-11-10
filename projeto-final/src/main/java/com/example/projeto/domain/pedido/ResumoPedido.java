package com.example.projeto.domain.pedido;

import java.math.BigDecimal;

/**
 * Resultado do calculo de um pedido (subtotal, desconto, frete e total).
 */
public record ResumoPedido(TipoCliente tipoCliente,
                           BigDecimal subtotal,
                           BigDecimal desconto,
                           BigDecimal frete,
                           BigDecimal total) {
}
