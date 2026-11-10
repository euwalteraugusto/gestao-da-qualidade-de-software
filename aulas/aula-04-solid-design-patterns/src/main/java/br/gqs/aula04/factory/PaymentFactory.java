package br.gqs.aula04.factory;

import java.util.Locale;

/** Factory Method de meios de pagamento (exercício do slide: CreditCard e Pix). */
public final class PaymentFactory {

    private PaymentFactory() {
    }

    /**
     * Cria o meio de pagamento pedido.
     *
     * @param type "CREDIT_CARD" ou "PIX" (sem diferenciar maiúsculas/minúsculas)
     * @return novo meio de pagamento
     * @throws IllegalArgumentException se o tipo for nulo ou desconhecido
     */
    public static Payment create(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid payment type: null");
        }
        return switch (type.trim().toUpperCase(Locale.ROOT)) {
            case "CREDIT_CARD" -> new CreditCard();
            case "PIX" -> new Pix();
            default -> throw new IllegalArgumentException("Invalid payment type: " + type);
        };
    }
}
