package com.example.projeto.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Geracao e validacao de tokens JWT (jjwt 0.12, HS256).
 * <p>
 * Dois tipos de token, distinguidos pela claim {@code typ}: {@code access} (curta
 * duracao, carrega os papeis) e {@code refresh} (longa duracao, so serve para obter
 * um novo par de tokens em {@code /auth/refresh}).
 */
@Service
public class JwtService {

    static final String CLAIM_TIPO = "typ";
    static final String CLAIM_ROLES = "roles";
    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";
    private static final int TAMANHO_MINIMO_CHAVE = 32;

    private final JwtProperties props;
    private final SecretKey chave;
    private final Clock clock;

    @Autowired
    public JwtService(JwtProperties props) {
        this(props, Clock.systemUTC());
    }

    JwtService(JwtProperties props, Clock clock) {
        byte[] bytes = props.secret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < TAMANHO_MINIMO_CHAVE) {
            throw new IllegalStateException(
                    "app.jwt.secret (JWT_SECRET) deve ter no minimo " + TAMANHO_MINIMO_CHAVE + " bytes para HS256");
        }
        this.props = props;
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.clock = clock;
    }

    public String gerarAccessToken(UserDetails usuario) {
        List<String> roles = usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return gerar(usuario.getUsername(), TIPO_ACCESS, props.accessExpiration(), roles);
    }

    public String gerarRefreshToken(UserDetails usuario) {
        return gerar(usuario.getUsername(), TIPO_REFRESH, props.refreshExpiration(), null);
    }

    public Duration validadeAccessToken() {
        return props.accessExpiration();
    }

    /**
     * Valida assinatura, emissor, expiracao e tipo do token.
     *
     * @throws TokenInvalidoException se qualquer verificacao falhar
     */
    public Claims validar(String token, String tipoEsperado) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .requireIssuer(props.issuer())
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!tipoEsperado.equals(claims.get(CLAIM_TIPO, String.class))) {
                throw new TokenInvalidoException("Tipo de token invalido: esperado " + tipoEsperado);
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidoException("Token invalido ou expirado", e);
        }
    }

    public String extrairUsername(Claims claims) {
        return claims.getSubject();
    }

    public Collection<SimpleGrantedAuthority> extrairRoles(Claims claims) {
        List<?> roles = claims.get(CLAIM_ROLES, List.class);
        if (roles == null) {
            return List.of();
        }
        return roles.stream().map(String::valueOf).map(SimpleGrantedAuthority::new).toList();
    }

    private String gerar(String username, String tipo, Duration validade, List<String> roles) {
        Instant agora = clock.instant();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(props.issuer())
                .subject(username)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(validade)))
                .claim(CLAIM_TIPO, tipo);
        if (roles != null) {
            builder.claim(CLAIM_ROLES, roles);
        }
        return builder.signWith(chave, Jwts.SIG.HS256).compact();
    }
}
