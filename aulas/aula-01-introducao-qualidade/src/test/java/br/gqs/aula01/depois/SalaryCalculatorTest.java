package br.gqs.aula01.depois;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.gqs.aula01.antes.CalculoSalario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SalaryCalculatorTest {

    private static final double DELTA = 1e-6;

    @ParameterizedTest(name = "bruto {0} -> liquido {1}")
    @CsvSource({"0,0", "500,475", "999.99,949.9905", "1000,900", "1999,1799.1", "2000,1700",
        "3999,3399.15", "4000,3200", "5000,4000"})
    void calculaLiquidoEmTodasAsFaixasEFronteiras(double bruto, double esperado) {
        assertEquals(esperado, SalaryCalculator.calculateNetSalary(bruto), DELTA);
    }

    @ParameterizedTest
    @CsvSource({"0,0.05", "999.99,0.05", "1000,0.10", "1999.99,0.10", "2000,0.15", "3999.99,0.15",
        "4000,0.20", "1000000,0.20"})
    void aliquotaPorFaixa(double salario, double aliquota) {
        assertEquals(aliquota, SalaryCalculator.getTaxRate(salario), 1e-12);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -1000, Double.NEGATIVE_INFINITY})
    void rejeitaSalarioNegativo(double bruto) {
        assertThrows(IllegalArgumentException.class, () -> SalaryCalculator.calculateNetSalary(bruto));
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY})
    void rejeitaNaNEInfinito(double bruto) {
        assertThrows(IllegalArgumentException.class, () -> SalaryCalculator.calculateNetSalary(bruto));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 1, 500, 999.99, 1000, 1500, 1999.99, 2000, 3000, 3999.99, 4000, 5000, 123456.78})
    void versaoLimpaEquivaleAVersaoRuimParaEntradasValidas(double bruto) {
        assertEquals(CalculoSalario.calcular(bruto), SalaryCalculator.calculateNetSalary(bruto), DELTA);
    }

    @Test
    void mainNaoLancaExcecao() {
        SalaryCalculator.main(new String[0]);
    }
}
