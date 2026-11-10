package br.gqs.aula11.depois;

/** PIS: 1,65% sobre o valor. */
public class PisStrategy extends AliquotaFixaStrategy {

    /** Cria a estratégia de PIS. */
    public PisStrategy() {
        super("0.0165");
    }
}
