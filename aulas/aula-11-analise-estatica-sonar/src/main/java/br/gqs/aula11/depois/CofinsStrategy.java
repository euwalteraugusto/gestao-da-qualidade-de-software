package br.gqs.aula11.depois;

/** COFINS: 7,6% acima de 5.000; 3% até esse valor. */
public class CofinsStrategy extends AliquotaPorFaixaStrategy {

    /** Cria a estratégia de COFINS. */
    public CofinsStrategy() {
        super("5000", "0.076", "0.03");
    }
}
