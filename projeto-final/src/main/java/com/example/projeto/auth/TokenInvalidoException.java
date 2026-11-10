package com.example.projeto.auth;

/**
 * Token JWT ausente, mal formado, expirado, com assinatura invalida ou do tipo errado.
 * (O slide usa {@code InvalidTokenException}, classe que nao existe no projeto.)
 */
public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException(String mensagem) {
        super(mensagem);
    }

    public TokenInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
