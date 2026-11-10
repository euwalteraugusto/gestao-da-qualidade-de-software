package br.gqs.aula04.factory;

/** Pagamento via Pix: sem taxa. */
public final class Pix implements Payment {

    @Override
    public String method() {
        return "PIX";
    }

    @Override
    public double charge(double amount) {
        PaymentValidator.requirePositive(amount);
        return amount;
    }
}
