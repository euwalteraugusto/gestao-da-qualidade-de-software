package br.gqs.aula04.relatorio;

import java.util.Objects;

/**
 * Orquestra a geração (delegada à estratégia de formato) e a persistência
 * (delegada ao repositório injetado). Cada responsabilidade está em sua classe (SRP).
 */
public class RelatorioService {

    private final RelatorioRepository repository;

    /**
     * Injeta a dependência de persistência (DIP).
     *
     * @param repository repositório, obrigatório
     */
    public RelatorioService(RelatorioRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    /**
     * Gera o relatório no formato informado (polimorfismo, sem {@code if/else}).
     *
     * @param formato estratégia de formato
     * @param dados conteúdo
     * @return relatório gerado
     */
    public String gerar(FormatoRelatorio formato, DadosRelatorio dados) {
        Objects.requireNonNull(formato, "formato");
        Objects.requireNonNull(dados, "dados");
        return formato.gerar(dados);
    }

    /**
     * Gera o relatório e o salva usando o título como chave.
     *
     * @param formato estratégia de formato
     * @param dados conteúdo
     * @return relatório gerado e salvo
     */
    public String gerarESalvar(FormatoRelatorio formato, DadosRelatorio dados) {
        String relatorio = gerar(formato, dados);
        repository.salvar(dados.titulo(), relatorio);
        return relatorio;
    }
}
