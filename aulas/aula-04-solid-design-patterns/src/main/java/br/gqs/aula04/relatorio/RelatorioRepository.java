package br.gqs.aula04.relatorio;

import java.util.List;
import java.util.Optional;

/** Abstração de persistência de relatórios (DIP): o serviço não conhece o banco concreto. */
public interface RelatorioRepository {

    /**
     * Salva (ou substitui) um relatório.
     *
     * @param titulo chave do relatório
     * @param conteudo texto já gerado
     */
    void salvar(String titulo, String conteudo);

    /**
     * Busca um relatório pelo título.
     *
     * @param titulo chave do relatório
     * @return conteúdo, se existir
     */
    Optional<String> buscar(String titulo);

    /**
     * Lista os títulos salvos, na ordem de inserção.
     *
     * @return títulos
     */
    List<String> listarTitulos();
}
