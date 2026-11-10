# Hotfix `hotfix/header-null` (Aula 9, Exercício da Atividade 1)

## Cenário

Em produção (`main`, após o release `v1.0.0`), `CabecalhoHttp.obterValor(cabecalhos, nome)` lança
`NullPointerException` quando o cabeçalho pedido não existe (`cabecalhos.get(nome)` devolve `null`
e o código chama `.trim()` direto). Isso derruba requisições sem o cabeçalho opcional.

Por que hotfix e não feature: o defeito está em produção e precisa sair sem esperar o próximo
release, então a branch nasce da `main` (e não da `develop`) e é mergeada de volta nas duas.

## Correção

Patch pronto, gerado e testado: [`hotfix/header-null.patch`](hotfix/header-null.patch).

| | Antes | Depois |
|---|---|---|
| Código | `return cabecalhos.get(nome).trim();` | `return Optional.ofNullable(cabecalhos.get(nome)).map(String::trim);` |
| Retorno | `String` (NPE se ausente) | `Optional<String>` (vazio se ausente ou nulo) |
| Testes | 1 (caminho feliz) | 3 (+ ausente, + valor `null`) |

Validação local do patch (aplicado sobre a árvore da aula): `mvn verify checkstyle:check` passa com
os 3 testes novos de `CabecalhoHttpTest`, e a regra de 100% da `NotificacaoService` segue satisfeita.

## Passo a passo (executado pelo orquestrador na raiz do repositório)

```bash
git checkout main && git pull origin main
git checkout -b hotfix/header-null                      # a partir de main
git apply --ignore-whitespace aulas/aula-09-git-flow-code-review/docs/hotfix/header-null.patch
git add aulas/aula-09-git-flow-code-review
git commit -m "fix(cabecalho): evita NullPointerException quando o cabecalho nao existe (issue-123)"
git push -u origin hotfix/header-null
# abrir PR hotfix/header-null -> main (aguardar CI verde), fazer merge
git tag -a v1.0.1 -m "Hotfix: header null"
git push origin v1.0.1
# trazer a correção para develop (evita regressão no próximo release)
git checkout develop && git pull origin develop
git merge --no-ff main -m "merge: hotfix header-null de main em develop"
git push origin develop
```

Resultado esperado: `main` e `develop` com a mesma correção; tag `v1.0.1`.

| Item a preencher | Valor |
|------------------|-------|
| Link do PR do hotfix | _a preencher_ |
| Commit do hotfix | _a preencher_ |
| Print do grafo de branches | _a preencher_ |
