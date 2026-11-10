package br.edu.iqs.aula07.usuario;

/** Falha de persistencia (embrulha {@link java.sql.SQLException}, preservando a causa). */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
