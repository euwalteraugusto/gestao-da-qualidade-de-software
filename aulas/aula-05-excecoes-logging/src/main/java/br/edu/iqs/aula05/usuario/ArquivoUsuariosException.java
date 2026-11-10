package br.edu.iqs.aula05.usuario;

/**
 * Falha ao gravar o arquivo de usuarios.
 * <p>
 * Checked de proposito: falha de I/O e algo que o chamador pode e deve tratar (escolher outro
 * caminho, tentar de novo, avisar o usuario). A causa original ({@code IOException}) e preservada.
 */
public class ArquivoUsuariosException extends Exception {

    public ArquivoUsuariosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
