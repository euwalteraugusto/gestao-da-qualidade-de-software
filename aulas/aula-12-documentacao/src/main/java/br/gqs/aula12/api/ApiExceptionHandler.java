package br.gqs.aula12.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Converte exceções de negócio em respostas HTTP padronizadas (RFC 9457, {@code ProblemDetail}). */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Cria o tratador de exceções. */
    public ApiExceptionHandler() {
        // gerenciado pelo Spring
    }

    /**
     * Mapeia usuário inexistente para HTTP 404.
     *
     * @param ex exceção lançada pelo serviço
     * @return detalhe do problema com status 404
     */
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(UsuarioNaoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
