package br.gqs.aula09;

/**
 * VERSÃO 1 (com erros intencionais) - commit inicial da branch feature/notificacoes.
 * Serve apenas para o PR falhar no CI (compila, mas os testes falham) e receber
 * comentários de code review. Guardado como .txt para não ser compilado aqui.
 */
public class NotificacaoService {

    public boolean emailValido(String email) {
        // BUG 1: NullPointerException se email for null
        // BUG 2: "@" sozinho não é validação de formato (aceita "@", "a@", "@b", "a b@c")
        return email.contains("@");
    }

    public String enviarEmail(String destinatario, String assunto, String corpo) {
        // BUG 3: não valida assunto/corpo
        // BUG 4: retorna texto genérico em vez de falhar rápido com exceção
        if (emailValido(destinatario)) {
            return "enviado";
        }
        return "falhou";
    }
}
