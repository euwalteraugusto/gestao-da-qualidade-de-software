package com.example.projeto.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo JWT completo (aula 18): login, acesso a recurso protegido, papeis, refresh token.
 * Usa os usuarios de desenvolvimento do perfil {@code dev} (senhas em docs/auth-guide.md).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    private static final String SENHA_ADMIN_DEV = "admin-dev-2026";
    private static final String SENHA_CLIENTE_DEV = "cliente-dev-2026";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private JsonNode login(String usuario, String senha) throws Exception {
        String corpo = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(usuario, senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.expiresIn", is(900)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo);
    }

    private static String bearer(JsonNode tokens) {
        return "Bearer " + tokens.get("accessToken").asText();
    }

    @Test
    void loginGeraJwtQueDaAcessoAoRecursoProtegido() throws Exception {
        JsonNode tokens = login("admin", SENHA_ADMIN_DEV);

        mockMvc.perform(get("/api/produtos").header(HttpHeaders.AUTHORIZATION, bearer(tokens)))
                .andExpect(status().isOk());
    }

    @Test
    void semTokenRetorna401EmJson() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is("Nao autenticado")));
    }

    @Test
    void tokenInvalidoRetorna401() throws Exception {
        mockMvc.perform(get("/api/produtos").header(HttpHeaders.AUTHORIZATION, "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void senhaErradaRetorna401() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail", is("Usuario ou senha invalidos")));
    }

    @Test
    void clienteLeMasNaoAlteraCatalogo() throws Exception {
        JsonNode tokens = login("cliente", SENHA_CLIENTE_DEV);

        mockMvc.perform(get("/api/produtos").header(HttpHeaders.AUTHORIZATION, bearer(tokens)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/produtos").header(HttpHeaders.AUTHORIZATION, bearer(tokens))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"X\",\"preco\":1.00}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Acesso negado")));
    }

    @Test
    void refreshTokenGeraNovoAccessToken() throws Exception {
        JsonNode tokens = login("cliente", SENHA_CLIENTE_DEV);

        String corpo = mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"%s\"}".formatted(tokens.get("refreshToken").asText())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/produtos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(objectMapper.readTree(corpo))))
                .andExpect(status().isOk());
    }

    @Test
    void accessTokenNaoServeComoRefreshToken() throws Exception {
        JsonNode tokens = login("cliente", SENHA_CLIENTE_DEV);

        mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"%s\"}".formatted(tokens.get("accessToken").asText())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenNaoServeComoAccessToken() throws Exception {
        JsonNode tokens = login("cliente", SENHA_CLIENTE_DEV);

        mockMvc.perform(get("/api/produtos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.get("refreshToken").asText()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sessaoWebSemTokenPrecisaDeCsrfParaAlterarApi() throws Exception {
        // AJAX da pagina (sessao) pode ler a API...
        mockMvc.perform(get("/api/produtos").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
        // ...mas escrita por sessao sem token CSRF e bloqueada (protecao CSRF mantida)
        mockMvc.perform(post("/api/produtos").with(user("admin").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"X\",\"preco\":1.00}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/produtos").with(user("admin").roles("ADMIN", "USER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"X\",\"preco\":1.00}"))
                .andExpect(status().isCreated());
    }

    @Test
    void paginasWebExigemLoginESwaggerEPublico() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/login"));
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void clienteNaoAcessaFormularioDeCadastroWeb() throws Exception {
        mockMvc.perform(get("/produtos/novo").with(user("cliente").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
