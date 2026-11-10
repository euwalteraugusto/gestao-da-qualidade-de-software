package br.gqs.aula04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import br.gqs.aula04.antes.RelatorioAntes;
import br.gqs.aula04.relatorio.DadosRelatorio;
import br.gqs.aula04.relatorio.FormatoHtml;
import br.gqs.aula04.relatorio.FormatoPdf;
import br.gqs.aula04.relatorio.FormatoRelatorio;
import br.gqs.aula04.relatorio.RelatorioRepository;
import br.gqs.aula04.relatorio.RelatorioRepositoryEmMemoria;
import br.gqs.aula04.relatorio.RelatorioService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Atividade 1: SRP (geração separada de persistência) e OCP (Strategy de formatos). */
class RelatorioTest {

    private final DadosRelatorio dados = new DadosRelatorio("Vendas", List.of("Item A", "Item B"));

    // ---- versão "antes" (caracterização) ----

    @Test
    void antesGeraPdfEHtmlPorIfElse() {
        RelatorioAntes antes = new RelatorioAntes();
        assertEquals("%PDF-SIMULADO\nVendas\n- Item A\n- Item B\n", antes.gerarRelatorio("PDF", dados));
        assertEquals("<html><body><h1>Vendas</h1><ul><li>Item A</li><li>Item B</li></ul></body></html>",
                antes.gerarRelatorio("HTML", dados));
    }

    @Test
    void antesNaoSuportaNovosFormatosSemEditarAClasse_violacaoDoOcp() {
        assertNull(new RelatorioAntes().gerarRelatorio("CSV", dados));
    }

    @Test
    void antesMisturaGeracaoEPersistenciaNaMesmaClasse_violacaoDoSrp() {
        RelatorioAntes antes = new RelatorioAntes();
        antes.salvarNoBanco(antes.gerarRelatorio("PDF", dados));
        assertEquals(1, antes.totalSalvos());
    }

    // ---- versão "depois" ----

    @Test
    void depoisProduzMesmaSaidaQueAntesParaPdfEHtml() {
        RelatorioAntes antes = new RelatorioAntes();
        RelatorioService service = new RelatorioService(new RelatorioRepositoryEmMemoria());
        assertEquals(antes.gerarRelatorio("PDF", dados), service.gerar(new FormatoPdf(), dados));
        assertEquals(antes.gerarRelatorio("HTML", dados), service.gerar(new FormatoHtml(), dados));
    }

    @Test
    void novoFormatoEAdicionadoSemModificarClassesExistentes_ocp() {
        FormatoRelatorio csv = d -> String.join(",", d.linhas());
        RelatorioService service = new RelatorioService(new RelatorioRepositoryEmMemoria());
        assertEquals("Item A,Item B", service.gerar(csv, dados));
    }

    @Test
    void htmlEscapaCaracteresEspeciais() {
        DadosRelatorio perigoso = new DadosRelatorio("<b>T&</b>", List.of("<script>x</script>"));
        String html = new FormatoHtml().gerar(perigoso);
        assertTrue(html.contains("&lt;b&gt;T&amp;&lt;/b&gt;"));
        assertTrue(html.contains("&lt;script&gt;x&lt;/script&gt;"));
    }

    @Test
    void gerarESalvarUsaOTituloComoChaveNoRepositorioReal() {
        RelatorioRepository repository = new RelatorioRepositoryEmMemoria();
        RelatorioService service = new RelatorioService(repository);
        String gerado = service.gerarESalvar(new FormatoPdf(), dados);
        assertEquals(Optional.of(gerado), repository.buscar("Vendas"));
        assertEquals(List.of("Vendas"), repository.listarTitulos());
        assertEquals(Optional.empty(), repository.buscar("Inexistente"));
    }

    @Test
    void servicoDependeDaAbstracaoEPodeUsarMock_dip() {
        RelatorioRepository mock = mock(RelatorioRepository.class);
        RelatorioService service = new RelatorioService(mock);
        String gerado = service.gerarESalvar(new FormatoPdf(), dados);
        verify(mock).salvar("Vendas", gerado);
    }

    @Test
    void gerarNaoPersisteNada() {
        RelatorioRepository mock = mock(RelatorioRepository.class);
        new RelatorioService(mock).gerar(new FormatoPdf(), dados);
        verify(mock, never()).salvar(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void validaArgumentos() {
        assertThrows(NullPointerException.class, () -> new RelatorioService(null));
        RelatorioService service = new RelatorioService(new RelatorioRepositoryEmMemoria());
        assertThrows(NullPointerException.class, () -> service.gerar(null, dados));
        assertThrows(NullPointerException.class, () -> service.gerar(new FormatoPdf(), null));
        assertThrows(IllegalArgumentException.class, () -> new DadosRelatorio(" ", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new DadosRelatorio(null, List.of()));
    }

    @Test
    void dadosFazemCopiaDefensivaENulosViramListaVazia() {
        java.util.ArrayList<String> origem = new java.util.ArrayList<>(List.of("a"));
        DadosRelatorio d = new DadosRelatorio("T", origem);
        origem.add("b");
        assertEquals(List.of("a"), d.linhas());
        assertEquals(List.of(), new DadosRelatorio("T", null).linhas());
    }
}
