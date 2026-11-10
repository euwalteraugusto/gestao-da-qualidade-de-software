package br.edu.iqs.aula08.autenticacao;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Servico de autenticacao em memoria para a feature "Autenticacao de usuario".
 * <p>
 * Diferente do slide (que sugere guardar a senha "1234" em texto), a senha e armazenada como hash
 * PBKDF2-HMAC-SHA256 com sal aleatorio. Continua sendo um exemplo DIDATICO: em producao use uma
 * biblioteca consolidada (Argon2/bcrypt via Spring Security) e persistencia real.
 * <p>
 * Regra extra: apos {@value #MAX_TENTATIVAS} senhas erradas seguidas a conta e bloqueada.
 */
public class AutenticacaoService {

    public static final int MAX_TENTATIVAS = 3;
    private static final int ITERACOES = 10_000;

    private record Credencial(byte[] sal, byte[] hash) {
    }

    private final Map<String, Credencial> credenciais = new HashMap<>();
    private final Map<String, Integer> falhasSeguidas = new HashMap<>();
    private final SecureRandom aleatorio = new SecureRandom();

    public void cadastrar(String login, String senha) {
        Objects.requireNonNull(login, "login");
        Objects.requireNonNull(senha, "senha");
        byte[] sal = new byte[16];
        aleatorio.nextBytes(sal);
        credenciais.put(login, new Credencial(sal, derivar(senha, sal)));
        falhasSeguidas.remove(login);
    }

    public ResultadoLogin autenticar(String login, String senha) {
        Credencial credencial = credenciais.get(login);
        if (credencial == null || senha == null) {
            return ResultadoLogin.NEGADO;
        }
        if (falhasSeguidas.getOrDefault(login, 0) >= MAX_TENTATIVAS) {
            return ResultadoLogin.BLOQUEADO;
        }
        // Comparacao em tempo constante (evita ataque de temporizacao)
        if (MessageDigest.isEqual(credencial.hash(), derivar(senha, credencial.sal()))) {
            falhasSeguidas.remove(login);
            return ResultadoLogin.CONCEDIDO;
        }
        int falhas = falhasSeguidas.merge(login, 1, Integer::sum);
        return falhas >= MAX_TENTATIVAS ? ResultadoLogin.BLOQUEADO : ResultadoLogin.NEGADO;
    }

    private static byte[] derivar(String senha, byte[] sal) {
        PBEKeySpec especificacao = new PBEKeySpec(senha.toCharArray(), sal, ITERACOES, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacao).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponível nesta JVM", e);
        } finally {
            especificacao.clearPassword();
        }
    }
}
