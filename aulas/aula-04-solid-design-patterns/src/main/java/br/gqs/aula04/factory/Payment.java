package br.gqs.aula04.factory;

/** Produto da {@link PaymentFactory}: uma forma de pagamento. */
public interface Payment {

    /**
     * Nome do método de pagamento.
     *
     * @return por exemplo {@code "CREDIT_CARD"}
     */
    String method();

    /**
     * Calcula o valor efetivamente cobrado, já com taxas do método.
     *
     * @param amount valor da compra, positivo
     * @return valor cobrado
     * @throws IllegalArgumentException se o valor não for positivo
     */
    double charge(double amount);
}
