package br.gqs.aula03.antes;

import java.util.Locale;

// VERSAO "ANTES" do desafio: if/else por tipo (viola OCP), metodo longo com varias
// responsabilidades (viola SRP), parametros t e v sem significado, aninhamento,
// numeros magicos e tipo desconhecido ignorado em silencio (ou NPE se t for nulo).
class Pagamento {
    void processar(String t, double v) {
        if(t.equals("CREDITO")) {
            if (v <= 0) {
                throw new IllegalArgumentException("valor invalido");
            }
            double taxa = v * 0.0299;
            double total = v + taxa;
            int p = 1;
            if (total > 1000) {
                p = 6;
            } else if (total > 300) {
                p = 3;
            }
            double parcela = total / p;
            System.out.println("CREDITO: total=" + String.format(Locale.US, "%.2f", total)
                + " em " + p + "x de " + String.format(Locale.US, "%.2f", parcela));
        } else if(t.equals("BOLETO")) {
            if (v <= 0) {
                throw new IllegalArgumentException("valor invalido");
            }
            double total = v + 3.5;
            System.out.println("BOLETO: total=" + String.format(Locale.US, "%.2f", total)
                + " vencimento em 3 dias");
        }
    }
}
