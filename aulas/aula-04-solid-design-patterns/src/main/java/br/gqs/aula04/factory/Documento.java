package br.gqs.aula04.factory;

/** Produto da fábrica de documentos. */
public interface Documento {

    /**
     * Tipo do documento.
     *
     * @return por exemplo {@code "PDF"}
     */
    String tipo();

    /**
     * Renderiza o conteúdo no formato do documento (simulado como texto).
     *
     * @param conteudo texto a renderizar
     * @return texto renderizado
     */
    String renderizar(String conteudo);
}
