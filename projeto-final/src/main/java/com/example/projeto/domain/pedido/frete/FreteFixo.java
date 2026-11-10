package com.example.projeto.domain.pedido.frete;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Frete fixo para pedidos abaixo de um valor minimo; acima dele o frete e gratis.
 * Os valores (antes "numeros magicos" 100 e 15 no codigo legado) vem de configuracao.
 */
public class FreteFixo implements CalculadoraFrete {

    private final BigDecimal valorMinimoFreteGratis;
    private final BigDecimal valorFrete;

    public FreteFixo(BigDecimal valorMinimoFreteGratis, BigDecimal valorFrete) {
        this.valorMinimoFreteGratis = Objects.requireNonNull(valorMinimoFreteGratis);
        this.valorFrete = Objects.requireNonNull(valorFrete);
    }

    @Override
    public BigDecimal calcular(BigDecimal total) {
        return total.compareTo(valorMinimoFreteGratis) < 0 ? valorFrete : BigDecimal.ZERO;
    }
}
