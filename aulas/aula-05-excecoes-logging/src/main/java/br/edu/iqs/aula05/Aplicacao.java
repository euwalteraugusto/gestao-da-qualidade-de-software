package br.edu.iqs.aula05;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.edu.iqs.aula05.banco.ContaService;
import br.edu.iqs.aula05.banco.LimiteSaqueExcedidoException;
import br.edu.iqs.aula05.banco.SaldoInsuficienteException;
import br.edu.iqs.aula05.usuario.ArquivoUsuariosException;
import br.edu.iqs.aula05.usuario.CadastroUsuario;
import br.edu.iqs.aula05.usuario.IdadeInvalidaException;

/**
 * Demonstracao executavel: {@code mvn -q compile exec:java -Dlog.dir=target/logs}.
 * <p>
 * Esta classe e a "fronteira" da aplicacao: e aqui que as excecoes de negocio sao tratadas e
 * registradas UMA unica vez (nada de catch-log-throw nas camadas internas).
 */
public final class Aplicacao {

    private static final Logger logger = LogManager.getLogger(Aplicacao.class);

    private Aplicacao() {
    }

    public static void main(String[] args) {
        ContaService origem = new ContaService("Ana", new BigDecimal("500.00"));
        ContaService destino = new ContaService("Bruno", new BigDecimal("100.00"));

        tentarTransferir(origem, destino, new BigDecimal("200.00"));   // sucesso
        tentarTransferir(origem, destino, new BigDecimal("900.00"));   // saldo insuficiente (resta 300)
        tentarTransferir(origem, destino, new BigDecimal("1500.00"));  // limite de saque excedido

        CadastroUsuario cadastro = new CadastroUsuario();
        cadastro.cadastrar("Carla", 30);
        try {
            cadastro.cadastrar("Davi", 16);
        } catch (IdadeInvalidaException e) {
            logger.warn("Cadastro rejeitado: {}", e.getMessage());
        }
        try {
            cadastro.salvarArquivo("target/usuarios-demo.txt");
        } catch (ArquivoUsuariosException e) {
            logger.error("Falha ao salvar arquivo de usuários", e);
        }

        try {
            throw new RuntimeException("Erro crítico!");
        } catch (RuntimeException e) {
            // Exemplo do slide (Sentry): com o appender Sentry ativo este ERROR seria enviado.
            logger.error("Erro capturado pelo Sentry (apenas log local nesta configuracao)", e);
        }
    }

    private static void tentarTransferir(ContaService origem, ContaService destino, BigDecimal valor) {
        try {
            origem.transferir(destino, valor);
        } catch (SaldoInsuficienteException | LimiteSaqueExcedidoException e) {
            // Unico ponto que registra a falha de negocio.
            logger.error("Falha na transferência: {}", e.getMessage());
        }
    }
}
