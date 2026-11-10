package br.gqs.aula12;

/**
 * Calculadora de operações financeiras básicas.
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * Financeira financeira = new Financeira();
 * double futuro = financeira.jurosCompostos(1000.0, 0.1, 2); // 1210.0 (aproximadamente)
 * }</pre>
 *
 * <p>A classe não guarda estado, portanto uma instância pode ser compartilhada entre threads.</p>
 *
 * @author Aluno da disciplina Introdução à Qualidade de Software
 * @version 1.1
 * @since 1.0
 * @see #jurosSimples(double, double, int)
 */
public class Financeira {

    /** Cria uma calculadora financeira (a classe não possui estado). */
    public Financeira() {
        // sem estado
    }

    /**
     * Calcula juros compostos: {@code principal * (1 + taxa)^meses}.
     *
     * <p>Exemplo: {@code jurosCompostos(1000, 0.1, 2)} devolve cerca de {@code 1210.0}.</p>
     *
     * @param principal valor inicial aplicado
     * @param taxa taxa mensal em forma decimal (0.1 = 10%)
     * @param meses período em meses (zero devolve o próprio principal)
     * @return valor futuro após o período informado
     * @throws IllegalArgumentException se {@code taxa} ou {@code meses} forem negativos
     * @see #jurosSimples(double, double, int)
     */
    public double jurosCompostos(double principal, double taxa, int meses) {
        validar(taxa, meses);
        return principal * Math.pow(1 + taxa, meses);
    }

    /**
     * Calcula juros simples: {@code principal * (1 + taxa * meses)}.
     *
     * <p>Exemplo: {@code jurosSimples(1000, 0.1, 2)} devolve {@code 1200.0}.</p>
     *
     * @param principal valor inicial aplicado
     * @param taxa taxa mensal em forma decimal (0.1 = 10%)
     * @param meses período em meses
     * @return valor futuro após o período informado
     * @throws IllegalArgumentException se {@code taxa} ou {@code meses} forem negativos
     * @see #jurosCompostos(double, double, int)
     */
    public double jurosSimples(double principal, double taxa, int meses) {
        validar(taxa, meses);
        return principal * (1 + taxa * meses);
    }

    private void validar(double taxa, int meses) {
        if (taxa < 0 || meses < 0) {
            throw new IllegalArgumentException("Parâmetros não podem ser negativos");
        }
    }
}
