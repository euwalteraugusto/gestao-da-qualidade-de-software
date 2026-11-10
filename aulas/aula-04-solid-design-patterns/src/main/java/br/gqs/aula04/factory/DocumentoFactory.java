package br.gqs.aula04.factory;

import java.util.Locale;

/**
 * Factory Method estático que centraliza a criação de documentos.
 *
 * <p>Correção em relação ao slide: o {@code switch} original lançava
 * {@code NullPointerException} para {@code tipo == null} e era sensível a
 * maiúsculas; agora {@code null} gera {@link IllegalArgumentException} e o tipo é
 * normalizado.
 */
public final class DocumentoFactory {

    private DocumentoFactory() {
    }

    /**
     * Cria o documento do tipo pedido.
     *
     * @param tipo "PDF" ou "DOCX" (sem diferenciar maiúsculas/minúsculas)
     * @return novo documento
     * @throws IllegalArgumentException se o tipo for nulo ou desconhecido
     */
    public static Documento criarDocumento(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Formato inválido: nulo");
        }
        return switch (tipo.trim().toUpperCase(Locale.ROOT)) {
            case "PDF" -> new Pdf();
            case "DOCX" -> new Docx();
            default -> throw new IllegalArgumentException("Formato inválido: " + tipo);
        };
    }
}
