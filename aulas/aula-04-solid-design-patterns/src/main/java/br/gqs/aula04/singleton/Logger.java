package br.gqs.aula04.singleton;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Logger como Singleton (exercício do slide), usando o idiom do <i>holder</i>:
 * thread-safe, preguiçoso e sem {@code synchronized} no acesso.
 *
 * <p>As mensagens ficam em memória (e podem ser consultadas), o que facilita
 * testar. O construtor é privado, então ninguém cria outra instância com {@code new}.
 */
public final class Logger {

    /** Níveis de log. */
    public enum Nivel { INFO, WARN, ERROR }

    private static final class Holder {
        private static final Logger INSTANCE = new Logger();
    }

    private final List<String> entradas = new CopyOnWriteArrayList<>();

    private Logger() {
    }

    /**
     * Retorna a instância única.
     *
     * @return instância única
     */
    public static Logger getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Registra uma mensagem no formato {@code [NIVEL] mensagem}.
     *
     * @param nivel nível da mensagem
     * @param mensagem texto, obrigatório
     */
    public void log(Nivel nivel, String mensagem) {
        if (nivel == null || mensagem == null) {
            throw new IllegalArgumentException("Nível e mensagem são obrigatórios.");
        }
        entradas.add("[" + nivel + "] " + mensagem);
    }

    /**
     * Registra mensagem de nível INFO.
     *
     * @param mensagem texto
     */
    public void info(String mensagem) {
        log(Nivel.INFO, mensagem);
    }

    /**
     * Registra mensagem de nível WARN.
     *
     * @param mensagem texto
     */
    public void warn(String mensagem) {
        log(Nivel.WARN, mensagem);
    }

    /**
     * Registra mensagem de nível ERROR.
     *
     * @param mensagem texto
     */
    public void error(String mensagem) {
        log(Nivel.ERROR, mensagem);
    }

    /**
     * Cópia imutável das mensagens registradas até agora.
     *
     * @return mensagens, na ordem de registro
     */
    public List<String> entradas() {
        return List.copyOf(entradas);
    }

    /** Apaga as mensagens (útil entre testes). */
    public void limpar() {
        entradas.clear();
    }
}
