package br.gqs.aula04.factory;

/**
 * Factory Method clássico (GoF), para a tarefa opcional: a classe base define o
 * algoritmo ({@link #gerar(String)}) e deixa para as subclasses decidir qual
 * {@link Documento} criar em {@link #criarDocumento()}.
 *
 * <p>Diferença para {@link DocumentoFactory}: lá a escolha é feita por um
 * parâmetro {@code String} (fábrica estática simples); aqui a escolha é feita por
 * polimorfismo, e novos tipos são novas subclasses (OCP).
 */
public abstract class GeradorDocumento {

    /**
     * Método fábrica: cada subclasse decide o tipo concreto.
     *
     * @return novo documento
     */
    protected abstract Documento criarDocumento();

    /**
     * Gera o documento final usando o produto criado pela subclasse.
     *
     * @param conteudo texto do documento
     * @return documento renderizado
     */
    public final String gerar(String conteudo) {
        return criarDocumento().renderizar(conteudo);
    }

    /** Gerador de PDF. */
    public static final class GeradorPdf extends GeradorDocumento {
        @Override
        protected Documento criarDocumento() {
            return new Pdf();
        }
    }

    /** Gerador de DOCX. */
    public static final class GeradorDocx extends GeradorDocumento {
        @Override
        protected Documento criarDocumento() {
            return new Docx();
        }
    }
}
