package br.gqs.aula09.hotfix;

import java.util.Map;

/**
 * Utilitário de leitura de cabeçalhos HTTP (cenário do hotfix/header-null da Aula 9).
 *
 * <p>ATENÇÃO: esta é a versão com o defeito de produção (issue-123): quando o
 * cabeçalho não existe, {@code obterValor} lança NullPointerException. A correção
 * está em {@code docs/hotfix/header-null.patch} e deve ser aplicada na branch
 * {@code hotfix/header-null}, criada a partir da {@code main}.</p>
 */
public class CabecalhoHttp {

    /**
     * Devolve o valor de um cabeçalho, sem espaços nas pontas.
     *
     * @param cabecalhos mapa de cabeçalhos
     * @param nome nome do cabeçalho
     * @return valor do cabeçalho sem espaços nas pontas
     */
    public String obterValor(Map<String, String> cabecalhos, String nome) {
        return cabecalhos.get(nome).trim();
    }
}
