package br.edu.iqs.aula06;

/** Classe utilitaria da tarefa da Aula 6 ({@code isEmpty} e {@code inverterString}). */
public final class StringUtils {

    private StringUtils() {
    }

    /** {@code true} se a string for nula ou tiver tamanho zero. Espacos NAO contam como vazio. */
    public static boolean isEmpty(String texto) {
        return texto == null || texto.isEmpty();
    }

    /**
     * Inverte a string. {@code null} retorna {@code null}. Usa {@link StringBuilder#reverse()},
     * que trata corretamente pares substitutos (emojis), mas nao agrupa caracteres combinantes.
     */
    public static String inverterString(String texto) {
        if (texto == null) {
            return null;
        }
        return new StringBuilder(texto).reverse().toString();
    }
}
