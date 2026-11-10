package com.example.projeto.auth;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String CHAVE_TESTE = "chave-de-teste-unitario-com-mais-de-32-bytes!!";
    private static final String HASH = "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5BWX4Z3TcuY3m3xR8iZ5uOQ8E8b6G";

    private final JwtProperties props =
            new JwtProperties(CHAVE_TESTE, "projeto-final", Duration.ofMinutes(15), Duration.ofDays(7));
    private final UserDetails admin = UsuarioFactory.criarAdmin("admin", HASH);

    @Test
    void deveGerarEValidarAccessTokenComRoles() {
        JwtService service = new JwtService(props);

        Claims claims = service.validar(service.gerarAccessToken(admin), JwtService.TIPO_ACCESS);

        assertThat(service.extrairUsername(claims)).isEqualTo("admin");
        assertThat(service.extrairRoles(claims)).containsExactlyInAnyOrder(
                new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        assertThat(service.validadeAccessToken()).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void refreshTokenNaoCarregaRolesENaoServeComoAccessToken() {
        JwtService service = new JwtService(props);
        String refresh = service.gerarRefreshToken(admin);

        assertThat(service.extrairRoles(service.validar(refresh, JwtService.TIPO_REFRESH))).isEmpty();
        assertThatThrownBy(() -> service.validar(refresh, JwtService.TIPO_ACCESS))
                .isInstanceOf(TokenInvalidoException.class);
    }

    @Test
    void deveRejeitarTokenExpirado() {
        Clock passado = Clock.fixed(Instant.now().minus(Duration.ofHours(2)), ZoneOffset.UTC);
        String token = new JwtService(props, passado).gerarAccessToken(admin);

        assertThatThrownBy(() -> new JwtService(props).validar(token, JwtService.TIPO_ACCESS))
                .isInstanceOf(TokenInvalidoException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void deveRejeitarTokenAssinadoComOutraChave() {
        JwtService outra = new JwtService(new JwtProperties("outra-chave-qualquer-com-mais-de-32-bytes!!",
                "projeto-final", Duration.ofMinutes(15), Duration.ofDays(7)));
        String token = outra.gerarAccessToken(admin);

        assertThatThrownBy(() -> new JwtService(props).validar(token, JwtService.TIPO_ACCESS))
                .isInstanceOf(TokenInvalidoException.class);
    }

    @Test
    void deveRejeitarTokenMalFormado() {
        JwtService service = new JwtService(props);
        assertThatThrownBy(() -> service.validar("nao-e-um-jwt", JwtService.TIPO_ACCESS))
                .isInstanceOf(TokenInvalidoException.class);
    }

    @Test
    void deveExigirChaveDeNoMinimo32Bytes() {
        JwtProperties fraca = new JwtProperties("mySecretKey123!", "projeto-final",
                Duration.ofMinutes(15), Duration.ofDays(7));
        assertThatThrownBy(() -> new JwtService(fraca))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }
}
