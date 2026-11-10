package br.gqs.aula04.imposto;

import java.util.Objects;

/**
 * Exemplo de Injeção de Dependência (DIP): o serviço recebe a abstração
 * {@link ImpostoStrategy} no construtor e não usa {@code new Icms()} internamente.
 * Trocar o imposto, ou usar um imposto falso nos testes, não exige alterar esta classe.
 */
public class NotaFiscalService {

    private final CalculadoraImposto calculadora;
    private final ImpostoStrategy imposto;

    /**
     * Cria o serviço.
     *
     * @param calculadora calculadora, obrigatória
     * @param imposto imposto aplicado às notas, obrigatório
     */
    public NotaFiscalService(CalculadoraImposto calculadora, ImpostoStrategy imposto) {
        this.calculadora = Objects.requireNonNull(calculadora, "calculadora");
        this.imposto = Objects.requireNonNull(imposto, "imposto");
    }

    /**
     * Total da nota: valor mais o imposto.
     *
     * @param valor valor dos produtos/serviços
     * @return valor total com imposto
     */
    public double totalComImposto(double valor) {
        return valor + calculadora.calcular(imposto, valor);
    }
}
