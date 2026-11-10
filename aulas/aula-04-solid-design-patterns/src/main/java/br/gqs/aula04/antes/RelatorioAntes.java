package br.gqs.aula04.antes;

import br.gqs.aula04.relatorio.DadosRelatorio;
import java.util.ArrayList;
import java.util.List;

/**
 * Versão "ANTES" do slide (mantida de propósito): viola SRP e OCP.
 *
 * <ul>
 *   <li><b>OCP:</b> cada novo formato exige editar o {@code if/else} desta classe.</li>
 *   <li><b>SRP:</b> a mesma classe gera relatório <em>e</em> cuida da persistência.</li>
 *   <li>Acoplamento: a persistência é um detalhe concreto interno (sem injeção).</li>
 * </ul>
 */
public class RelatorioAntes {

    private final List<String> banco = new ArrayList<>();

    /**
     * Gera o relatório no formato pedido, decidindo por {@code if/else}.
     *
     * @param tipo "PDF" ou "HTML"
     * @param dados conteúdo do relatório
     * @return relatório gerado
     */
    public String gerarRelatorio(String tipo, DadosRelatorio dados) {
        if (tipo.equals("PDF")) {
            StringBuilder sb = new StringBuilder("%PDF-SIMULADO\n").append(dados.titulo()).append('\n');
            for (String linha : dados.linhas()) {
                sb.append("- ").append(linha).append('\n');
            }
            return sb.toString();
        } else if (tipo.equals("HTML")) {
            StringBuilder sb = new StringBuilder("<html><body><h1>").append(dados.titulo())
                    .append("</h1><ul>");
            for (String linha : dados.linhas()) {
                sb.append("<li>").append(linha).append("</li>");
            }
            return sb.append("</ul></body></html>").toString();
        }
        return null;
    }

    /**
     * Persiste o relatório (responsabilidade que não pertence a esta classe).
     *
     * @param conteudo relatório já gerado
     */
    public void salvarNoBanco(String conteudo) {
        banco.add(conteudo);
    }

    /**
     * Quantidade de relatórios salvos.
     *
     * @return total salvo
     */
    public int totalSalvos() {
        return banco.size();
    }
}
