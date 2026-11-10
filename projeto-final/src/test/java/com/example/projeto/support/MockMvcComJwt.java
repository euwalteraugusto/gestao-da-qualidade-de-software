package com.example.projeto.support;

import com.example.projeto.auth.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Cria um {@link MockMvc} que envia, em toda requisicao, um access token JWT REAL do
 * usuario informado - os testes da API passam pela cadeia de seguranca de verdade
 * (filtro JWT + {@code @PreAuthorize}), sem atalhos como {@code @WithMockUser}.
 */
public final class MockMvcComJwt {

    private MockMvcComJwt() {
    }

    public static MockMvc criar(WebApplicationContext contexto, String username) {
        JwtService jwtService = contexto.getBean(JwtService.class);
        UserDetailsService usuarios = contexto.getBean(UserDetailsService.class);
        String token = jwtService.gerarAccessToken(usuarios.loadUserByUsername(username));
        return MockMvcBuilders.webAppContextSetup(contexto)
                .apply(springSecurity())
                .defaultRequest(get("/").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .build();
    }
}
