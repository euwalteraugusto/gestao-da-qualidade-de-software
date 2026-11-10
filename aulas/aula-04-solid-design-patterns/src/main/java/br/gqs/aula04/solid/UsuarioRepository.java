package br.gqs.aula04.solid;

import java.util.ArrayList;
import java.util.List;

/**
 * SRP: a persistência de usuários fica numa classe própria; {@link Usuario} apenas
 * representa o dado. Aqui o armazenamento é uma lista em memória.
 */
public class UsuarioRepository {

    private final List<String> armazenamento = new ArrayList<>();

    /**
     * Persiste qualquer objeto {@link Salvavel} (depende da abstração, DIP).
     *
     * @param salvavel objeto a salvar
     */
    public void salvar(Salvavel salvavel) {
        salvavel.salvarEm(armazenamento);
    }

    /**
     * Nomes salvos até agora.
     *
     * @return cópia imutável do armazenamento
     */
    public List<String> listar() {
        return List.copyOf(armazenamento);
    }
}
