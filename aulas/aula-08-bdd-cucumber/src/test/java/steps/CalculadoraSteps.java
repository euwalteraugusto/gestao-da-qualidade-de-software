package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.edu.iqs.aula08.calculadora.Calculadora;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

/**
 * Step definitions da feature calculadora.feature.
 * O Cucumber cria uma instancia nova desta classe para CADA cenario, entao o estado abaixo nao
 * vaza de um cenario para outro.
 */
public class CalculadoraSteps {

    private int num1;
    private int num2;
    private double resultado;
    private ArithmeticException excecao; // capturamos o tipo especifico, nao Exception generica

    @Dado("que eu tenho os números {int} e {int}")
    public void definir_numeros(int a, int b) {
        this.num1 = a;
        this.num2 = b;
    }

    @Quando("eu peço para somar")
    public void somar() {
        this.resultado = Calculadora.somar(num1, num2);
    }

    @Quando("eu somo estes números")
    public void somar_estes_numeros() {
        somar();
    }

    @Quando("eu peço para dividir")
    public void dividir() {
        try {
            this.resultado = Calculadora.dividir(num1, num2);
        } catch (ArithmeticException e) {
            this.excecao = e; // guardada para verificacao posterior (passo "Então")
        }
    }

    // O slide usa {int} e um campo int; como dividir() devolve double, usamos {double}.
    // Em features com "# language: pt" o decimal e escrito com virgula (2,25).
    @Então("o resultado deve ser {double}")
    public void verificar_resultado(double esperado) {
        assertNull(excecao, "não deveria ter ocorrido erro");
        assertEquals(esperado, resultado, 1e-9);
    }

    @Então("o sistema deve informar {string}")
    public void verificar_mensagem(String mensagem) {
        assertNotNull(excecao, "era esperado um erro");
        assertEquals(mensagem, excecao.getMessage());
    }

    @E("nenhum erro deve ter ocorrido")
    public void nenhum_erro() {
        assertNull(excecao);
    }
}
