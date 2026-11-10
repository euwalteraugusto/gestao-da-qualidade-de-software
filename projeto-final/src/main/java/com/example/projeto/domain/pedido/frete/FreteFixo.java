package com.example.projeto.domain.pedido.frete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Frete fixo para pedidos abaixo de um valor minimo; acima dele o frete e gratis.
 * Os valores (antes "numeros magicos" 100 e 15 no codigo legado) vem de configuracao.
 */
public class FreteFixo implements CalculadoraFrete {

    private static final BigDecimal FRETE_GRATIS = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final BigDecimal valorMinimoFreteGratis;
    private final BigDecimal valorFrete;

    public FreteFixo(BigDecimal valorMinimoFreteGratis, BigDecimal valorFrete) {
        this.valorMinimoFreteGratis = Objects.requireNonNull(valorMinimoFreteGratis);
        // Escala monetaria fixa: o YAML le "15.00" como 15.0 (ajuste da aula 17)
        this.valorFrete = Objects.requireNonNull(valorFrete).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcular(BigDecimal total) {
        return total.compareTo(valorMinimoFreteGratis) < 0 ? valorFrete : FRETE_GRATIS;
    }
}
