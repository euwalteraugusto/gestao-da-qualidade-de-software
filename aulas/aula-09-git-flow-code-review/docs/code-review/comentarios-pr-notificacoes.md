**Revisão do PR `feature/notificacoes` -> `develop`**

Resumo: a ideia está certa, mas há pontos bloqueantes. Detalhes:

1. **bloqueante:** `destinatario.contains("@")` aceita `@`, `a@` e `a b@c`. Sugiro regex compilada uma vez:
   ```java
   private static final Pattern EMAIL_VALIDO =
           Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$");
   ```
2. **bloqueante:** com `destinatario == null` o método lança `NullPointerException` na primeira linha. Trate `null` como e-mail inválido.
3. **sugestão:** retornar `"falhou"` obriga o chamador a comparar texto. Prefira `throw new IllegalArgumentException("E-mail inválido: " + destinatario)` (fail fast).
4. **bloqueante:** assunto e corpo não são validados; um e-mail vazio seria "enviado".
5. **bloqueante:** o critério do desafio é 100% de cobertura da classe nova; o CI (`Aula 09 - PR Validation`) está vermelho por causa dos testes.
6. **nit:** falta Javadoc com `@param`/`@throws`.

Peço alterações antes de aprovar.
