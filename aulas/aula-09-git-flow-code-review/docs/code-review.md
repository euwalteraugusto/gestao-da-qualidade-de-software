# Code review (Aula 9, Atividade 3 e Desafio Colaborativo)

Contexto: como o trabalho é individual, os papéis Dev 1 (autor), Dev 2 (abre o PR) e Dev 3
(revisor) foram assumidos em momentos distintos pelo mesmo aluno. Os comentários abaixo são os
que foram (ou serão, no PR real) postados na revisão.

## 1. PR #123 - `Calculadora.divide`

### Antes (código revisado)

```java
public class Calculadora {
    public int divide(int a, int b) {
        return a / b; // Problema grave: falta tratamento de divisão por zero!
    }
}
```

Registro: `src/main/java/br/gqs/aula09/revisao/antes/Calculadora.java`.
Comportamento medido: `ArithmeticException: / by zero` (mensagem genérica da JVM, sem contexto).

### Depois (código aprovado)

```java
public int divide(int a, int b) {
    if (b == 0) {
        throw new ArithmeticException("Divisor não pode ser zero"); // fail fast
    }
    return a / b;
}
```

Registro: `src/main/java/br/gqs/aula09/Calculadora.java` + `CalculadoraTest`
(3 testes, incluindo `assertThrows` com checagem da mensagem).
Saída real da comparação: `evidencias/antes-depois-divide.txt`.

### Comentários de review exemplares

Regra do slide: comentário **específico**, com **motivo** e **sugestão em snippet**; nunca "não gostei".

| Tipo | Comentário ruim | Comentário bom (usado) |
|------|-----------------|------------------------|
| Bug | "Evite divisão sem tratamento de erro" | "`divide(1, 0)` lança `ArithmeticException: / by zero`, sem dizer qual argumento causou o problema. Sugiro validar o divisor no início (fail fast): `if (b == 0) { throw new ArithmeticException("Divisor não pode ser zero"); }`" |
| Teste | "Faltam testes" | "Não há teste para o caminho de erro. Sugiro `assertThrows(ArithmeticException.class, () -> calc.divide(1, 0))` e conferir a mensagem, para que a regra não seja removida sem querer." |
| Contrato | "Documente" | "Adicione `@throws ArithmeticException se b for zero` ao Javadoc: quem chama precisa saber que o método pode falhar." |
| Dúvida (não bloqueante) | "Isso está estranho" | "Pergunta: queremos divisão inteira truncada (`7/3 = 2`)? Se sim, vale um teste que documente isso." |
| Elogio | - | "Boa a mensagem de erro em português e específica; facilita o diagnóstico em log." |

Prefixos usados nos comentários (convenção de equipe): `bloqueante:`, `sugestão:`, `dúvida:`, `nit:` (detalhe estético).

## 2. Desafio: `feature/notificacoes` (`NotificacaoService.enviarEmail`)

### Versão 1 (PR com erros intencionais)

Arquivo guardado em `docs/code-review/NotificacaoService.v1-com-erro.java.txt`. Problemas e comentário do revisor:

| Linha / ponto | Comentário do revisor (Dev 3) | Resolução |
|---------------|-------------------------------|-----------|
| `destinatario.contains("@")` | "bloqueante: `contains("@")` aceita `@`, `a@` e `a b@c`. Sugiro regex, ex.: `^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$`, compilada uma vez em `static final Pattern`." | Regex `EMAIL_VALIDO` |
| `destinatario` nulo | "bloqueante: com `null` o método lança `NullPointerException` na primeira linha. Trate `null` como e-mail inválido." | `emailValido` testa `null` |
| retorno `"falhou"` | "sugestão: retornar String de erro obriga o chamador a comparar texto. Prefira `IllegalArgumentException` com a causa (fail fast)." | `throw new IllegalArgumentException(...)` |
| assunto/corpo | "bloqueante: assunto e corpo não são validados; e-mail vazio seria 'enviado'." | `vazio(assunto) \|\| vazio(corpo)` |
| sem testes | "bloqueante: critério do desafio é 100% de cobertura na classe nova." | `NotificacaoServiceTest` (26 casos) + regra JaCoCo |
| Javadoc | "nit: falta Javadoc com `@param`/`@throws`." | Javadoc completo |

CI do PR (workflow `Aula 09 - PR Validation`) fica **vermelho** na v1 porque os testes finais
não passam contra o código com bug; depois do commit de correção fica verde.

### Versão 2 (aprovada)

`src/main/java/br/gqs/aula09/NotificacaoService.java`:
cobertura de `NotificacaoService` = **10/10 linhas e 14/14 ramos (100%)**, verificada pela regra
`jacoco:check` do `pom.xml` (ver `evidencias/jacoco-resumo.txt`).

Critérios de sucesso do slide:

- [x] todos os comentários resolvidos (tabela acima)
- [x] build passando (`mvn clean verify`: 30 testes antes do hotfix (32 depois), 0 falhas, 0 violações Checkstyle)
- [x] 100% de cobertura para a nova classe

## 3. Checklist de code review (usar em todo PR)

Funcionalidade
- [ ] Implementa o requisito/critério de aceite do ticket
- [ ] Casos de borda tratados (null, vazio, zero, limites, negativos)
- [ ] Erros tratados de forma explícita (fail fast, mensagem útil, sem engolir exceção)

Legibilidade e boas práticas
- [ ] Nomes claros; métodos curtos e com uma responsabilidade
- [ ] Sem duplicação, sem código morto ou comentado, sem "números mágicos"
- [ ] Segue padrão do projeto (Checkstyle sem violações)

Testes
- [ ] Testes unitários para caminho feliz **e** de erro
- [ ] Cobertura adequada (meta da classe nova; não reduz a cobertura geral)
- [ ] Testes determinísticos, rápidos e independentes

Segurança e desempenho
- [ ] Entrada externa validada; nenhum segredo/credencial no código
- [ ] Sem dependências novas sem justificativa

Documentação e processo
- [ ] Javadoc/README atualizados quando o comportamento muda
- [ ] Mensagens de commit descritivas (Conventional Commits)
- [ ] PR pequeno, com descrição, link da issue e screenshot/saída de teste
- [ ] CI verde e conversas resolvidas antes do merge

## 4. Como dar e receber feedback

1. Critique o código, não a pessoa; use "nós" e perguntas.
2. Classifique a gravidade (`bloqueante`, `sugestão`, `dúvida`, `nit`).
3. Sempre que possível, traga snippet com a alternativa.
4. Autor responde a cada comentário (corrigido em `<hash>` ou justificativa) antes de resolver a conversa.
