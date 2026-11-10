package br.gqs.aula12.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Aplicação Spring Boot da Aula 12 (sobe em {@code http://localhost:8080}). */
@SpringBootApplication
public class Aula12Application {

    /**
     * Inicia a aplicação.
     *
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(Aula12Application.class, args);
    }
}
