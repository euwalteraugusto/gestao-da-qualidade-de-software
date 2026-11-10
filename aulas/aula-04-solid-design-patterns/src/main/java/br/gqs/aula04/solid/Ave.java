package br.gqs.aula04.solid;

/**
 * LSP (Liskov): a base {@code Ave} só promete o que <b>toda</b> ave faz (comer).
 * Voar foi para a interface {@link Voador}; assim {@link Pinguim} não herda um
 * método {@code voar()} que não consegue cumprir.
 */
public abstract class Ave {

    /**
     * Nome da ave.
     *
     * @return nome
     */
    public abstract String nome();

    /**
     * Toda ave come.
     *
     * @return descrição da ação
     */
    public String comer() {
        return nome() + " está comendo";
    }
}
