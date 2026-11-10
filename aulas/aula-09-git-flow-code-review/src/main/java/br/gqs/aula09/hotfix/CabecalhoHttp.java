package br.gqs.aula09.hotfix;

import java.util.Map;
import java.util.Optional;

/**
 * Utilitário de leitura de cabeçalhos HTTP (cenário do hotfix/header-null da Aula 9).
 *
 * <p>Versão corrigida (hotfix da issue-123): cabeçalho ausente deixa de causar
 * NullPointerException e passa a ser representado por {@link Optional#empty()}.</p>
 */
public class CabecalhoHttp {

    /**
     * Devolve o valor de um cabeçalho, sem espaços nas pontas.
     *
     * @param cabecalhos mapa de cabeçalhos (não pode ser {@code null})
     * @param nome nome do cabeçalho
     * @return valor sem espaços nas pontas, ou vazio se o cabeçalho não existir ou for {@code null}
     */
    public Optional<String> obterValor(Map<String, String> cabecalhos, String nome) {
        return Optional.ofNullable(cabecalhos.get(nome)).map(String::trim);
    }
}
