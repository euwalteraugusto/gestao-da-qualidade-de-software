package br.gqs.aula11.antes;

import java.util.List;

/**
 * ANTES da refatoração (segunda classe da tarefa de casa).
 *
 * <p>Smells intencionais: ifs aninhados (4 níveis), concatenação de String em laço, literais
 * repetidos, laço com índice, valores monetários em double, formatos duplicados em dois pontos.</p>
 */
public class RelatorioVendas {

    public String gerar(List<Venda> vendas, String formato) {
        String saida = "";
        double total = 0;
        for (int i = 0; i < vendas.size(); i++) {
            Venda v = vendas.get(i);
            if (v != null) {
                if (v.getValor() > 0) {
                    if (formato.equals("CSV")) {
                        saida = saida + v.getCliente() + ";" + v.getValor() + "\n";
                    } else if (formato.equals("TXT")) {
                        saida = saida + "Cliente: " + v.getCliente() + " - R$ " + v.getValor() + "\n";
                    } else if (formato.equals("HTML")) {
                        saida = saida + "<tr><td>" + v.getCliente() + "</td><td>" + v.getValor() + "</td></tr>\n";
                    }
                    total = total + v.getValor();
                }
            }
        }
        if (formato.equals("CSV")) {
            saida = saida + "TOTAL;" + total + "\n";
        } else if (formato.equals("TXT")) {
            saida = saida + "Total geral: R$ " + total + "\n";
        } else if (formato.equals("HTML")) {
            saida = saida + "<tr><td>TOTAL</td><td>" + total + "</td></tr>\n";
        }
        return saida;
    }
}
