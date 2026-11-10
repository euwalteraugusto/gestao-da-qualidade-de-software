package br.gqs.aula04.solid;

import java.util.List;
import java.util.Objects;

/**
 * Usuário: implementa as duas interfaces pequenas (ISP). Encapsulamento com atributo
 * privado e imutável; sem lógica de banco de dados (SRP, ver {@link UsuarioRepository}).
 */
public final class Usuario implements Logavel, Salvavel {

    private final String nome;

    /**
     * Cria um usuário.
     *
     * @param nome nome, obrigatório
     */
    public Usuario(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        this.nome = nome;
    }

    /**
     * Nome do usuário.
     *
     * @return nome
     */
    public String getNome() {
        return nome;
    }

    @Override
    public String mensagemDeLog() {
        return "Usuario[" + nome + "]";
    }

    @Override
    public void salvarEm(List<String> destino) {
        Objects.requireNonNull(destino, "destino").add(nome);
    }
}
