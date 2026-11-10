package com.example.projeto.domain.pedido.frete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * <b>Decorator</b> (tarefa pos-aula 16 - "Decorator para adicionar logging"):
 * envolve qualquer {@link CalculadoraFrete} e adiciona log + medicao de tempo
 * sem alterar a implementacao decorada nem quem a utiliza.
 */
public class CalculadoraFreteComLog implements CalculadoraFrete {

    private static final Logger log = LoggerFactory.getLogger(CalculadoraFreteComLog.class);

    private final CalculadoraFrete decorada;

    public CalculadoraFreteComLog(CalculadoraFrete decorada) {
        this.decorada = Objects.requireNonNull(decorada);
    }

    @Override
    public BigDecimal calcular(BigDecimal total) {
        long inicio = System.nanoTime();
        BigDecimal frete = decorada.calcular(total);
        log.info("Frete calculado: total={} frete={} calculadora={} ({} us)",
                total, frete, decorada.getClass().getSimpleName(), (System.nanoTime() - inicio) / 1_000);
        return frete;
    }
}
