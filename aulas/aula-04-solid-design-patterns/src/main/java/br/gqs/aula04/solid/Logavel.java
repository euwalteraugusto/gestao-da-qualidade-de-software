package br.gqs.aula04.solid;

/**
 * ISP: interface pequena só de log. Em vez de uma interface "gorda" com dezenas
 * de métodos, cada cliente depende apenas do que usa ({@code Logavel}, {@link Salvavel}).
 */
public interface Logavel {

    /**
     * Texto a ser registrado no log.
     *
     * @return mensagem de log
     */
    String mensagemDeLog();
}
