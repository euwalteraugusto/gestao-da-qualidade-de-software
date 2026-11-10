package br.edu.iqs.aula07.usuario;

import java.util.Objects;

/** Usuario do sistema. O {@code id} e gerado pelo banco quando o usuario e salvo. */
public class Usuario {

    private Long id;
    private final String nome;

    public Usuario(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario outro)) {
            return false;
        }
        return Objects.equals(id, outro.id) && Objects.equals(nome, outro.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "'}";
    }
}
