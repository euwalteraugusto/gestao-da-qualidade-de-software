package com.example.projeto.interfaces.rest;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST de produtos.
 * <p>
 * Pacote {@code interfaces.rest}: o slide usa {@code interface.rest}, mas
 * {@code interface} e palavra reservada do Java e nao compila como nome de pacote.
 */
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @GetMapping
    public List<Produto> listar() {
        return service.listarTodos();
    }

    /** US-001: GET /api/produtos/buscar?nome=Xiaomi */
    @GetMapping("/buscar")
    public List<Produto> buscarPorNome(@RequestParam String nome) {
        return service.buscarPorNome(nome);
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto criar(@Valid @RequestBody ProdutoRequest request) {
        return service.cadastrar(request.toEntity());
    }

    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return service.atualizar(id, request.toEntity());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
