package br.gqs.aula11.depois;

/** ISS: 5% sobre o valor. */
public class IssStrategy extends AliquotaFixaStrategy {

    /** Cria a estratégia de ISS. */
    public IssStrategy() {
        super("0.05");
    }
}
