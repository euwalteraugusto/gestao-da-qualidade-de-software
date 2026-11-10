package br.gqs.aula04.imposto;

import java.util.Objects;

/**
 * Calculadora que delega a regra à estratégia recebida (polimorfismo).
 * Depende só da abstração {@link ImpostoStrategy} (DIP), nunca de {@link Icms} ou {@link Iss}.
 */
public class CalculadoraImposto {

    /**
     * Calcula o imposto com a estratégia informada.
     *
     * @param imposto estratégia de cálculo, obrigatória
     * @param valor base de cálculo, não negativa
     * @return valor do imposto
     * @throws IllegalArgumentException se o valor for negativo, NaN ou infinito
     */
    public double calcular(ImpostoStrategy imposto, double valor) {
        Objects.requireNonNull(imposto, "imposto");
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo.");
        }
        return imposto.calcular(valor);
    }
}
