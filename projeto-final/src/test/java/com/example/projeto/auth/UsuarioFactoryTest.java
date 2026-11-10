package com.example.projeto.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioFactoryTest {

    private static final String HASH = "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5BWX4Z3TcuY3m3xR8iZ5uOQ8E8b6G";

    @Test
    void adminRecebeAdminEUser() {
        UserDetails admin = UsuarioFactory.criarAdmin("admin", HASH);
        assertThat(admin.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        assertThat(admin.getPassword()).startsWith("{bcrypt}$2");
    }

    @Test
    void clienteRecebeSomenteUser() {
        assertThat(UsuarioFactory.criarCliente("cliente", HASH).getAuthorities())
                .extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_USER");
    }

    @Test
    void naoAceitaSenhaEmTextoPuro() {
        assertThatThrownBy(() -> UsuarioFactory.criarAdmin("admin", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BCrypt");
        assertThatThrownBy(() -> UsuarioFactory.criarCliente("c", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
