package br.gqs.aula01.antes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

/**
 * Testes de caracterizacao da versao ruim: documentam o comportamento atual,
 * inclusive as falhas que a refatoracao corrige.
 */
class VersaoAntesTest {

    private static final double DELTA = 1e-9;

    @ParameterizedTest(name = "bruto {0} -> liquido {1}")
    @CsvSource({"500,475", "999.99,949.9905", "1000,900", "1999,1799.1", "2000,1700", "3999,3399.15",
        "4000,3200", "5000,4000"})
    void calculoSalarioSegueAsFaixas(double bruto, double esperado) {
        assertEquals(esperado, CalculoSalario.calcular(bruto), 1e-6);
    }

    @Test
    void calculoSalarioAceitaNegativoSemValidar_falhaConhecida() {
        double liquido = CalculoSalario.calcular(-1000);
        assertTrue(liquido > -1000, "o 'liquido' fica maior que o bruto negativo: bug que a versao limpa corrige");
        assertEquals(-950.0, liquido, DELTA);
    }

    @Test
    void calcTotalSemDescontoAteOLimite() {
        OrderProcessor p = new OrderProcessor();
        assertEquals(1000.0, p.calcTotal(new double[] {500, 250}, new int[] {1, 2}), DELTA);
    }

    @Test
    void calcTotalComDescontoAcimaDoLimite() {
        OrderProcessor p = new OrderProcessor();
        assertEquals(1350.0, p.calcTotal(new double[] {500, 250}, new int[] {2, 2}), DELTA);
    }

    @Test
    void calcTotalComArraysDeTamanhosDiferentesFalhaDeFormaObscura_falhaConhecida() {
        OrderProcessor p = new OrderProcessor();
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> p.calcTotal(new double[] {10, 20}, new int[] {1}));
    }

    @Test
    void calcTotalComArrayNuloLancaNullPointer_falhaConhecida() {
        OrderProcessor p = new OrderProcessor();
        assertThrows(NullPointerException.class, () -> p.calcTotal(null, new int[] {1}));
    }
}
