package br.gqs.aula04.factory;

/** Validação compartilhada pelos meios de pagamento (evita duplicação, DRY). */
final class PaymentValidator {

    private PaymentValidator() {
    }

    static void requirePositive(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }
}
