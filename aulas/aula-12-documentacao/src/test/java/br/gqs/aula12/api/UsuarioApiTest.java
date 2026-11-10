package br.gqs.aula12.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioApiTest {

    private static final String JSON_ANA = "{\"nome\":\"Ana Souza\",\"email\":\"ana@exemplo.com\"}";

    @Autowired
    private MockMvc mvc;

    @Test
    void criaEConsultaUsuario() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(JSON_ANA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Ana Souza"));

        mvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@exemplo.com"));
    }

    @Test
    void usuarioInexistenteDevolve404ComProblemDetail() throws Exception {
        mvc.perform(get("/usuarios/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Usuário 9999 não encontrado"));
    }

    @Test
    void corpoInvalidoDevolve400() throws Exception {
        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"email\":\"nao-e-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void documentacaoOpenApiDescreveOsDoisEndpointsComExemplos() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(containsString("3.")))
                .andExpect(jsonPath("$.paths['/usuarios/{id}'].get.summary").value("Obter usuário por ID"))
                .andExpect(jsonPath("$.paths['/usuarios'].post.summary").value("Criar usuário"))
                .andExpect(jsonPath("$.paths['/usuarios'].post.responses['201']").exists())
                .andExpect(content().string(containsString("ana@exemplo.com")));
    }
}
