package br.gqs.aula01.depois;

/**
 * Versao "DEPOIS" (Clean Code) do processador de pedidos.
 *
 * <p>Correcao em relacao ao slide: a constante {@code DISCOUNT_RATE = 0.9} era
 * na verdade um <em>fator</em> (90% do valor); foi separada em taxa de desconto
 * (0.10) e calculada de forma explicita. Tambem foram adicionadas validacoes de
 * nulos, tamanhos diferentes, precos e quantidades negativos.
 */
public class OrderProcessor {

    /** Subtotal estritamente acima deste valor recebe desconto. */
    static final double DISCOUNT_THRESHOLD = 1000.0;
    /** Desconto de 10%. */
    static final double DISCOUNT_RATE = 0.10;

    /**
     * Calcula o total do pedido, aplicando desconto se o subtotal passar do limite.
     *
     * @param prices precos unitarios
     * @param quantities quantidades de cada item (mesmo tamanho de {@code prices})
     * @return total com desconto, se elegivel
     * @throws IllegalArgumentException se algum array for nulo, tiverem tamanhos
     *     diferentes ou houver preco/quantidade negativos
     */
    public double calculateTotal(double[] prices, int[] quantities) {
        validate(prices, quantities);
        double subtotal = calculateSubtotal(prices, quantities);
        return applyDiscount(subtotal);
    }

    private double calculateSubtotal(double[] prices, int[] quantities) {
        double subtotal = 0;
        for (int i = 0; i < prices.length; i++) {
            subtotal += prices[i] * quantities[i];
        }
        return subtotal;
    }

    private double applyDiscount(double subtotal) {
        if (subtotal > DISCOUNT_THRESHOLD) {
            return subtotal * (1 - DISCOUNT_RATE);
        }
        return subtotal;
    }

    private void validate(double[] prices, int[] quantities) {
        if (prices == null || quantities == null) {
            throw new IllegalArgumentException("Prices and quantities must not be null.");
        }
        if (prices.length != quantities.length) {
            throw new IllegalArgumentException(
                    "Prices and quantities arrays must have the same length.");
        }
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < 0 || Double.isNaN(prices[i])) {
                throw new IllegalArgumentException("Price cannot be negative: index " + i);
            }
            if (quantities[i] < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative: index " + i);
            }
        }
    }
}
