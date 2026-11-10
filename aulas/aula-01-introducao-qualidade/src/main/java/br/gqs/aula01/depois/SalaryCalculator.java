package br.gqs.aula01.depois;

/**
 * Versao "DEPOIS" (Clean Code) do calculo de salario liquido.
 *
 * <p>Melhorias: nomes descritivos, metodos pequenos, constantes nomeadas no
 * lugar de numeros magicos e validacao de entrada.
 */
public final class SalaryCalculator {

    /** Faixas salariais (limite superior exclusivo) e suas aliquotas. */
    static final double LIMITE_FAIXA_1 = 1000.0;
    static final double LIMITE_FAIXA_2 = 2000.0;
    static final double LIMITE_FAIXA_3 = 4000.0;

    static final double ALIQUOTA_FAIXA_1 = 0.05;
    static final double ALIQUOTA_FAIXA_2 = 0.10;
    static final double ALIQUOTA_FAIXA_3 = 0.15;
    static final double ALIQUOTA_FAIXA_4 = 0.20;

    private SalaryCalculator() {
    }

    public static void main(String[] args) {
        double grossSalary = 5000.0;
        double netSalary = calculateNetSalary(grossSalary);
        System.out.println("Net Salary: " + netSalary);
    }

    /**
     * Calcula o salario liquido (bruto menos o desconto da faixa).
     *
     * @param grossSalary salario bruto, nao negativo e finito
     * @return salario liquido
     * @throws IllegalArgumentException se o salario for negativo, NaN ou infinito
     */
    public static double calculateNetSalary(double grossSalary) {
        validate(grossSalary);
        double taxAmount = grossSalary * getTaxRate(grossSalary);
        return grossSalary - taxAmount;
    }

    /**
     * Retorna a aliquota aplicavel ao salario informado.
     *
     * @param salary salario bruto ja validado
     * @return aliquota entre 0.05 e 0.20
     */
    static double getTaxRate(double salary) {
        if (salary < LIMITE_FAIXA_1) {
            return ALIQUOTA_FAIXA_1;
        }
        if (salary < LIMITE_FAIXA_2) {
            return ALIQUOTA_FAIXA_2;
        }
        if (salary < LIMITE_FAIXA_3) {
            return ALIQUOTA_FAIXA_3;
        }
        return ALIQUOTA_FAIXA_4;
    }

    private static void validate(double grossSalary) {
        if (Double.isNaN(grossSalary) || Double.isInfinite(grossSalary)) {
            throw new IllegalArgumentException("Salary must be a finite number.");
        }
        if (grossSalary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
    }
}
