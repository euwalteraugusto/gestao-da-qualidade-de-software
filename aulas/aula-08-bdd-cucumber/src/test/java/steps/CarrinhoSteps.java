package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import br.edu.iqs.aula08.carrinho.Carrinho;
import br.edu.iqs.aula08.carrinho.CupomInvalidoException;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

/** Step definitions da feature carrinho.feature (tarefa: feature para o projeto final). */
public class CarrinhoSteps {

    private Carrinho carrinho;
    private CupomInvalidoException erroCupom;
    private IllegalArgumentException erroItem;

    @Dado("que tenho um carrinho vazio")
    public void carrinho_vazio() {
        carrinho = new Carrinho();
    }

    // "Quando" e "E" sao apenas palavras-chave da feature; em Cucumber um passo vale para qualquer
    // uma delas, entao UMA definicao atende "Quando eu adiciono..." e "E eu adiciono...".
    @Quando("eu adiciono {int} unidade(s) de {string} a R$ {bigdecimal}")
    public void adicionar(int quantidade, String nome, BigDecimal preco) {
        try {
            carrinho.adicionar(nome, preco, quantidade);
        } catch (IllegalArgumentException e) {
            erroItem = e;
        }
    }

    @Quando("eu aplico o cupom {string}")
    public void aplicar_cupom(String codigo) {
        try {
            carrinho.aplicarCupom(codigo);
        } catch (CupomInvalidoException e) {
            erroCupom = e;
        }
    }

    @Então("o total do carrinho deve ser R$ {bigdecimal}")
    public void verificar_total(BigDecimal esperado) {
        // compareTo ignora a escala (27 == 27,00)
        assertEquals(0, esperado.compareTo(carrinho.getTotal()),
                "esperado " + esperado + " mas o total é " + carrinho.getTotal());
    }

    @E("o carrinho deve ter {int} item/itens")
    public void verificar_itens(int esperado) {
        assertEquals(esperado, carrinho.getQuantidadeItens());
    }

    @Então("o sistema deve recusar o cupom com a mensagem {string}")
    public void cupom_recusado(String mensagem) {
        assertNotNull(erroCupom, "o cupom deveria ter sido recusado");
        assertEquals(mensagem, erroCupom.getMessage());
    }

    @Então("o sistema deve recusar o item com a mensagem {string}")
    public void item_recusado(String mensagem) {
        assertNotNull(erroItem, "o item deveria ter sido recusado");
        assertEquals(mensagem, erroItem.getMessage());
    }
}
