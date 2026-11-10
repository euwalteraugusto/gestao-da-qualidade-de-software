package br.gqs.aula11.depois;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DEPOIS da refatoração: cada imposto é uma estratégia registrada num mapa, e o método
 * principal não tem mais cadeia de if/else (complexidade ciclomática baixa).
 *
 * <p>Correção em relação ao slide: {@code Map.of(...).containsKey(null)} lança
 * NullPointerException, então o tipo nulo é tratado antes da consulta.</p>
 */
public class FinanceUtils {

    private final Map<String, ImpostoStrategy> estrategias = Map.of(
            "ICMS", new IcmsStrategy(),
            "IPI", new IpiStrategy(),
            "ISS", new IssStrategy(),
            "PIS", new PisStrategy(),
            "COFINS", new CofinsStrategy());

    /**
     * Calcula o imposto de um valor.
     *
     * @param valor valor base
     * @param tipo sigla do imposto (ICMS, IPI, ISS, PIS ou COFINS)
     * @return imposto com duas casas; {@link BigDecimal#ZERO} se valor/tipo forem nulos ou o tipo for desconhecido
     */
    public BigDecimal calcularImposto(BigDecimal valor, String tipo) {
        ImpostoStrategy estrategia = tipo == null ? null : estrategias.get(tipo);
        if (valor == null || estrategia == null) {
            return BigDecimal.ZERO;
        }
        return estrategia.calcular(valor);
    }
}
