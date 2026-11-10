package br.gqs.aula11.depois;

/** ICMS: 17% sobre o valor. */
public class IcmsStrategy extends AliquotaFixaStrategy {

    /** Cria a estratégia de ICMS. */
    public IcmsStrategy() {
        super("0.17");
    }
}
