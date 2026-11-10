package br.edu.iqs.aula05.usuario;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desafio da Aula 5: versao refatorada do {@code CadastroUsuario} do slide.
 * <p>
 * O que mudou:
 * <ol>
 *   <li>{@code System.err.println("Usuário menor de idade")} virou {@link IdadeInvalidaException}
 *       (excecao customizada) - o erro deixa de ser ignorado silenciosamente;</li>
 *   <li>logging com SLF4J (parametrizado com {@code {}}), sem {@code System.out/err} nem
 *       {@code printStackTrace()};</li>
 *   <li>{@code salvarArquivo} usa try-with-resources (o {@code FileWriter} e fechado sempre,
 *       inclusive quando o {@code close()} falha) e traduz {@link IOException} para
 *       {@link ArquivoUsuariosException} preservando a causa.</li>
 * </ol>
 * Observacao: o slide original nem compilaria - {@code fw.close()} no {@code finally} lanca
 * {@code IOException} nao tratada.
 * <p>
 * Regra anti catch-log-throw: os metodos ou registram o erro, ou o propagam; nunca os dois.
 * Quem decide registrar a falha e a camada que a trata (ver {@code Aplicacao}).
 */
public class CadastroUsuario {

    public static final int IDADE_MINIMA = 18;
    public static final int IDADE_MAXIMA = 130;

    private static final Logger logger = LoggerFactory.getLogger(CadastroUsuario.class);

    private final List<Usuario> cadastrados = new ArrayList<>();

    /**
     * Cadastra um usuario maior de idade.
     *
     * @throws IllegalArgumentException se o nome for nulo ou em branco
     * @throws IdadeInvalidaException   se a idade for negativa, maior que {@value #IDADE_MAXIMA}
     *                                  ou menor que {@value #IDADE_MINIMA}
     */
    public Usuario cadastrar(String nome, int idade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (idade < 0 || idade > IDADE_MAXIMA) {
            throw new IdadeInvalidaException(idade,
                    "deve estar entre 0 e " + IDADE_MAXIMA + " anos");
        }
        if (idade < IDADE_MINIMA) {
            throw new IdadeInvalidaException(idade,
                    "usuário menor de idade (mínimo " + IDADE_MINIMA + " anos)");
        }
        Usuario usuario = new Usuario(nome.strip(), idade);
        cadastrados.add(usuario);
        logger.info("Usuário cadastrado: {} ({} anos)", usuario.nome(), usuario.idade());
        return usuario;
    }

    public List<Usuario> getCadastrados() {
        return List.copyOf(cadastrados);
    }

    /**
     * Grava os usuarios cadastrados em arquivo texto (uma linha "nome;idade" por usuario).
     *
     * @throws ArquivoUsuariosException se nao for possivel escrever no caminho informado
     */
    public void salvarArquivo(String path) throws ArquivoUsuariosException {
        try (FileWriter fw = new FileWriter(path, StandardCharsets.UTF_8)) {
            for (Usuario usuario : cadastrados) {
                fw.write(usuario.nome() + ";" + usuario.idade() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new ArquivoUsuariosException("Não foi possível salvar os usuários em: " + path, e);
        }
        logger.debug("{} usuário(s) gravado(s) em {}", cadastrados.size(), path);
    }
}
