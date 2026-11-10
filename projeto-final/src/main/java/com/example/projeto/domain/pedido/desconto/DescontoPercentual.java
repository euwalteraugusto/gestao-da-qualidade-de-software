package com.example.projeto.domain.pedido.desconto;

import com.example.projeto.domain.pedido.TipoCliente;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Base comum das estrategias de desconto percentual (evita duplicar a conta e o
 * arredondamento monetario em cada estrategia).
 */
abstract class DescontoPercentual implements PoliticaDesconto {

    private final TipoCliente tipoCliente;
    private final BigDecimal fator;

    protected DescontoPercentual(TipoCliente tipoCliente, String fator) {
        this.tipoCliente = tipoCliente;
        this.fator = new BigDecimal(fator);
    }

    @Override
    public TipoCliente tipoCliente() {
        return tipoCliente;
    }

    @Override
    public BigDecimal aplicarDesconto(BigDecimal total) {
        return total.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }
}
