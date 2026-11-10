package com.example.projeto.domain.service;

import com.example.projeto.domain.model.Produto;
import com.example.projeto.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negocio de produtos. Os controllers deixam de acessar o repositorio
 * diretamente (separacao de responsabilidades - camada de servico).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository repository;

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    /** US-001: busca por parte do nome, sem diferenciar maiusculas/minusculas. */
    public List<Produto> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Produto cadastrar(Produto produto) {
        produto.setId(null);
        return repository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto dados) {
        Produto existente = buscarPorId(id);
        existente.setNome(dados.getNome());
        existente.setPreco(dados.getPreco());
        return repository.save(existente);
    }

    @Transactional
    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new ProdutoNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
