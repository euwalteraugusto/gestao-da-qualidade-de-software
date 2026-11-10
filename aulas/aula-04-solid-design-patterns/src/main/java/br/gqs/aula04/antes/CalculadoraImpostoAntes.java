package br.gqs.aula04.antes;

/**
 * Versão "ANTES" do desafio (mantida de propósito): viola o OCP, pois cada novo
 * imposto exige alterar este método, e devolve 0 em silêncio para tipos
 * desconhecidos, escondendo erros.
 */
public class CalculadoraImpostoAntes {

    /**
     * Calcula o imposto por {@code if/else}.
     *
     * @param tipo "ICMS" ou "ISS"
     * @param valor base de cálculo
     * @return imposto, ou 0 se o tipo for desconhecido
     */
    public double calcular(String tipo, double valor) {
        if (tipo.equals("ICMS")) {
            return valor * 0.10;
        } else if (tipo.equals("ISS")) {
            return valor * 0.05;
        }
        return 0;
    }
}
