package br.gqs.aula04.factory;

/** Pagamento com cartão de crédito: taxa de 2,99% sobre a compra. */
public final class CreditCard implements Payment {

    static final double FEE_RATE = 0.0299;

    @Override
    public String method() {
        return "CREDIT_CARD";
    }

    @Override
    public double charge(double amount) {
        PaymentValidator.requirePositive(amount);
        return amount * (1 + FEE_RATE);
    }
}
