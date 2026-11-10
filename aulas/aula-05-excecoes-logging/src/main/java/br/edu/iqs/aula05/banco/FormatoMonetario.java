package br.edu.iqs.aula05.banco;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formata valores em reais (R$ 1.234,56) para compor mensagens de erro legiveis.
 */
final class FormatoMonetario {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private FormatoMonetario() {
    }

    static String formatar(BigDecimal valor) {
        // NumberFormat nao e thread-safe; por isso e criado a cada chamada.
        NumberFormat formato = NumberFormat.getNumberInstance(PT_BR);
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return "R$ " + formato.format(valor);
    }
}
