package br.gqs.aula04.solid;

import java.util.List;

/** ISP: interface pequena só de persistência. */
public interface Salvavel {

    /**
     * Salva o objeto na lista de destino (persistência simulada).
     *
     * @param destino lista que representa o armazenamento
     */
    void salvarEm(List<String> destino);
}
