package br.gqs.aula04.solid;

/** Capacidade de voar (separada de {@link Ave} para respeitar o LSP). */
public interface Voador {

    /**
     * Voa.
     *
     * @return descrição da ação
     */
    String voar();
}
