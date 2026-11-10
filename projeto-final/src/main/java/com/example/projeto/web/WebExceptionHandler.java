package com.example.projeto.web;

import com.example.projeto.integration.CepNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

/**
 * Tratamento de erros das paginas web (Thymeleaf). Restrito ao pacote {@code web}
 * para nao interferir na API REST, que responde JSON (ProblemDetail).
 * <p>
 * Diferenca em relacao ao slide: alem de mostrar a pagina de erro, define o status
 * HTTP correto (o slide devolvia a pagina "error" com status 200).
 */
@ControllerAdvice(basePackages = "com.example.projeto.web")
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);
    private static final String VIEW_ERRO = "error";

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public String handleApiError(RestClientException ex, Model model) {
        log.warn("Falha ao consultar API externa: {}", ex.getMessage());
        model.addAttribute("error", "Falha ao consultar API externa");
        model.addAttribute("status", HttpStatus.BAD_GATEWAY.value());
        return VIEW_ERRO;
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCepNaoEncontrado(CepNaoEncontradoException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        return VIEW_ERRO;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEntradaInvalida(IllegalArgumentException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        return VIEW_ERRO;
    }
}
