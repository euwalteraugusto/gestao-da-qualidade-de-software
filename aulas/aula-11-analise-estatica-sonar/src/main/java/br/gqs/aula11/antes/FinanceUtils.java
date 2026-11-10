package br.gqs.aula11.antes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * ANTES da refatoração: código com code smells reais (alvo da análise estática).
 *
 * <p>Smells intencionais: complexidade ciclomática alta, if/else encadeado, números mágicos,
 * strings repetidas, NullPointerException potencial ({@code tipo} nulo), campo público mutável,
 * campo e variável sem uso, import sem uso, System.out em lógica de negócio.</p>
 */
public class FinanceUtils {

    public String ultimoErro = null;

    private int contador;

    // Complexidade ciclomática alta
    public BigDecimal calcularImposto(BigDecimal valor, String tipo) {
        if (valor == null) return BigDecimal.ZERO;
        BigDecimal resultado;
        int tentativas = 0;
        List<String> historico = new ArrayList<>();
        if (tipo.equals("ICMS")) {
            resultado = valor.multiply(new BigDecimal("0.17"));
        } else if (tipo.equals("IPI")) {
            if (valor.compareTo(new BigDecimal("10000")) > 0) {
                resultado = valor.multiply(new BigDecimal("0.10"));
            } else {
                resultado = valor.multiply(new BigDecimal("0.05"));
            }
        } else if (tipo.equals("ISS")) {
            resultado = valor.multiply(new BigDecimal("0.05"));
        } else if (tipo.equals("PIS")) {
            resultado = valor.multiply(new BigDecimal("0.0165"));
        } else if (tipo.equals("COFINS")) {
            if (valor.compareTo(new BigDecimal("5000")) > 0) {
                resultado = valor.multiply(new BigDecimal("0.076"));
            } else {
                resultado = valor.multiply(new BigDecimal("0.03"));
            }
        } else {
            ultimoErro = "tipo de imposto desconhecido: " + tipo;
            System.out.println(ultimoErro);
            return BigDecimal.ZERO;
        }
        return resultado.setScale(2, RoundingMode.HALF_UP);
    }
}
