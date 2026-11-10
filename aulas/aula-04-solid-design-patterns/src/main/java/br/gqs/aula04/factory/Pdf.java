package br.gqs.aula04.factory;

/** Documento PDF (simulado). */
public final class Pdf implements Documento {

    @Override
    public String tipo() {
        return "PDF";
    }

    @Override
    public String renderizar(String conteudo) {
        return "%PDF-SIMULADO " + conteudo;
    }
}
