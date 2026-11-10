package br.gqs.aula04.factory;

/** Documento DOCX (simulado). */
public final class Docx implements Documento {

    @Override
    public String tipo() {
        return "DOCX";
    }

    @Override
    public String renderizar(String conteudo) {
        return "[DOCX] " + conteudo;
    }
}
