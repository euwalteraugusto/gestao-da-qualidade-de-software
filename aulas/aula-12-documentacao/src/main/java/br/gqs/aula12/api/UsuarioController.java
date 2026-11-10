package br.gqs.aula12.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints REST de usuários. */
@RestController
@Tag(name = "Usuários", description = "Cadastro e consulta de usuários")
public class UsuarioController {

    private final UsuarioService service;

    /**
     * Cria o controlador.
     *
     * @param service regras de usuários
     */
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * Obtém um usuário pelo identificador.
     *
     * @param id identificador do usuário
     * @return o usuário encontrado
     */
    @Operation(summary = "Obter usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/usuarios/{id}")
    public Usuario getUsuario(@Parameter(description = "ID do usuário", example = "1") @PathVariable Long id) {
        return service.buscar(id);
    }

    /**
     * Cria um usuário.
     *
     * @param pedido dados do novo usuário
     * @return o usuário criado, já com identificador
     */
    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário e devolve o registro com ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos (nome vazio ou e-mail mal formado)",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do usuário a criar",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = NovoUsuarioRequest.class),
                    examples = @ExampleObject(name = "Exemplo válido",
                            value = "{\"nome\": \"Ana Souza\", \"email\": \"ana@exemplo.com\"}")))
    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario criarUsuario(@Valid @RequestBody NovoUsuarioRequest pedido) {
        return service.criar(pedido);
    }
}
