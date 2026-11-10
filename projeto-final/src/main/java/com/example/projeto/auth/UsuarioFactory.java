package com.example.projeto.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <b>Factory Method</b> para criacao de usuarios (tarefa pos-aula 16).
 * Centraliza quais papeis cada perfil recebe; quem cria usuarios nao monta
 * listas de roles "na mao" (evita, por exemplo, um admin sem o papel USER).
 * As senhas chegam sempre como hash BCrypt - nunca em texto puro.
 */
public final class UsuarioFactory {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private UsuarioFactory() {
    }

    /** Administrador: le e altera o catalogo. */
    public static UserDetails criarAdmin(String username, String senhaHashBcrypt) {
        return criar(username, senhaHashBcrypt, ROLE_ADMIN, ROLE_USER);
    }

    /** Cliente: somente leitura do catalogo e calculo de pedidos. */
    public static UserDetails criarCliente(String username, String senhaHashBcrypt) {
        return criar(username, senhaHashBcrypt, ROLE_USER);
    }

    private static UserDetails criar(String username, String senhaHashBcrypt, String... roles) {
        if (senhaHashBcrypt == null || !senhaHashBcrypt.startsWith("$2")) {
            throw new IllegalArgumentException("A senha do usuario '" + username + "' deve ser um hash BCrypt");
        }
        return User.withUsername(username)
                .password("{bcrypt}" + senhaHashBcrypt)
                .roles(roles)
                .build();
    }
}
