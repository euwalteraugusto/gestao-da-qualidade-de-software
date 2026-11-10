package br.gqs.aula11.depois;

/** IPI: 10% acima de 10.000; 5% até esse valor. */
public class IpiStrategy extends AliquotaPorFaixaStrategy {

    /** Cria a estratégia de IPI. */
    public IpiStrategy() {
        super("10000", "0.10", "0.05");
    }
}
