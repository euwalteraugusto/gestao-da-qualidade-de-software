package br.gqs.aula04.singleton;

/**
 * Singleton da conexão com o banco (versão do slide: inicialização preguiçosa com
 * {@code synchronized}).
 *
 * <p>A conexão é simulada (não há banco real). {@code getInstance} sincronizado é
 * simples e correto; o custo é bloquear em toda chamada. A classe {@link Logger}
 * mostra a alternativa sem esse custo (idiom do <i>holder</i>).
 */
public final class DatabaseConnection {

    private static DatabaseConnection instance;

    private boolean connected;

    private DatabaseConnection() {
    }

    /**
     * Retorna a instância única, criando-a na primeira chamada (lazy).
     *
     * @return instância única
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /** Abre a conexão (simulada). Chamar mais de uma vez não tem efeito extra. */
    public synchronized void connect() {
        connected = true;
    }

    /** Fecha a conexão (simulada). */
    public synchronized void disconnect() {
        connected = false;
    }

    /**
     * Informa se a conexão está aberta.
     *
     * @return {@code true} se conectado
     */
    public synchronized boolean isConnected() {
        return connected;
    }
}
