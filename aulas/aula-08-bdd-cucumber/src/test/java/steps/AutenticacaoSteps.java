package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import br.edu.iqs.aula08.autenticacao.AutenticacaoService;
import br.edu.iqs.aula08.autenticacao.ResultadoLogin;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Mas;
import io.cucumber.java.pt.Quando;

/** Step definitions da feature autenticacao.feature (desafio da aula). */
public class AutenticacaoSteps {

    private final AutenticacaoService servico = new AutenticacaoService();
    private String ultimoLogin;
    private ResultadoLogin resultado;

    @Dado("que o usuário {string} com senha {string} está cadastrado")
    public void usuario_cadastrado(String login, String senha) {
        servico.cadastrar(login, senha);
    }

    @Quando("eu faço login com {string} e {string}")
    public void fazer_login(String login, String senha) {
        ultimoLogin = login;
        resultado = servico.autenticar(login, senha);
    }

    @Quando("eu erro a senha de {string} {int} vezes seguidas")
    public void errar_senha_varias_vezes(String login, int vezes) {
        ultimoLogin = login;
        for (int i = 1; i <= vezes; i++) {
            resultado = servico.autenticar(login, "senha-errada-" + i);
        }
    }

    @Então("o acesso é concedido")
    public void acesso_concedido() {
        assertEquals(ResultadoLogin.CONCEDIDO, resultado);
    }

    @Então("o acesso é negado")
    public void acesso_negado() {
        assertEquals(ResultadoLogin.NEGADO, resultado);
    }

    @Então("a conta é bloqueada")
    public void conta_bloqueada() {
        assertEquals(ResultadoLogin.BLOQUEADO, resultado);
    }

    @Então("a conta não está bloqueada")
    public void conta_nao_bloqueada() {
        assertNotEquals(ResultadoLogin.BLOQUEADO, resultado);
    }

    @Mas("ao tentar entrar com a senha correta {string} o acesso continua negado")
    public void senha_correta_continua_negada(String senha) {
        assertNotEquals(ResultadoLogin.CONCEDIDO, servico.autenticar(ultimoLogin, senha));
    }
}
