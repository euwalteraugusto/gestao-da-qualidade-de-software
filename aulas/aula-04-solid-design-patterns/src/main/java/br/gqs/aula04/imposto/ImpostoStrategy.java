package br.gqs.aula04.imposto;

/** Strategy: contrato comum dos impostos (cada imposto tem a sua regra). */
public interface ImpostoStrategy {

    /**
     * Calcula o imposto sobre o valor.
     *
     * @param valor base de cálculo, não negativa
     * @return valor do imposto
     */
    double calcular(double valor);
}
