package br.gqs.aula04.relatorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Implementação em memória do repositório (substituível por JDBC/JPA sem tocar no serviço). */
public class RelatorioRepositoryEmMemoria implements RelatorioRepository {

    private final Map<String, String> armazenamento = new LinkedHashMap<>();

    @Override
    public synchronized void salvar(String titulo, String conteudo) {
        armazenamento.put(titulo, conteudo);
    }

    @Override
    public synchronized Optional<String> buscar(String titulo) {
        return Optional.ofNullable(armazenamento.get(titulo));
    }

    @Override
    public synchronized List<String> listarTitulos() {
        return new ArrayList<>(armazenamento.keySet());
    }
}
