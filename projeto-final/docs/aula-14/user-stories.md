# User Stories - API de Produtos

Template usado (aula 14): *"Como [perfil], eu quero [acao] para [beneficio]"*.
Priorizacao MoSCoW: **Must**, **Should**, **Could**, **Won't**.

---

## US-001 - Busca de produtos por nome (implementada na aula 14)

**Como** cliente, **eu quero** buscar produtos por parte do nome **para** encontrar rapidamente o item que procuro.

- **Prioridade:** Must have
- **Labels sugeridas:** `must-have`, `backend`, `sprint-1`

**Criterios de aceitacao**
- [x] `GET /produtos/buscar?nome=Xiaomi` retorna os produtos cujo nome contem o texto
- [x] A busca ignora maiusculas/minusculas (`xiaomi` encontra `Smartphone Xiaomi`)
- [x] Sem resultados retorna `200` com lista vazia `[]`
- [x] Coberta por teste de integracao (`ProdutoControllerIT#deveBuscarProdutosPorNome`)

**Tasks**
- [x] Implementar query method `findByNomeContainingIgnoreCase`
- [x] Criar endpoint `GET /produtos/buscar`
- [x] Escrever testes de integracao

---

## US-002 - Validacao de campos obrigatorios no cadastro (adicional)

**Como** administrador do catalogo, **eu quero** que o sistema recuse produtos sem nome ou com preco invalido **para** manter o catalogo consistente.

- **Prioridade:** Must have
- **Labels sugeridas:** `must-have`, `backend`, `qualidade`

**Criterios de aceitacao**
- [ ] `POST /produtos` sem `nome` (ou com nome em branco) retorna `400 Bad Request`
- [ ] `preco` nulo, zero ou negativo retorna `400 Bad Request`
- [ ] O corpo do erro informa o campo invalido e a mensagem em portugues
- [ ] Teste automatizado cobrindo cada regra

**Tasks**
- [ ] Adicionar Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`)
- [ ] Criar handler global de erros (`@RestControllerAdvice`)
- [ ] Testes de validacao (unitario + integracao)

> Implementada na aula 15 (tarefa pos-aula "teste para validacao de campos obrigatorios").

---

## US-003 - Calculo do total de um pedido com desconto por tipo de cliente (adicional)

**Como** cliente VIP ou PREMIUM, **eu quero** ver o total do meu pedido ja com o meu desconto e o frete **para** saber quanto vou pagar antes de finalizar.

- **Prioridade:** Should have
- **Labels sugeridas:** `should-have`, `backend`, `refactor`

**Criterios de aceitacao**
- [ ] VIP recebe 10% de desconto; PREMIUM recebe 20%; REGULAR nao tem desconto
- [ ] Pedidos com total (apos desconto) abaixo de R$ 100,00 pagam frete fixo de R$ 15,00
- [ ] Novos tipos de desconto podem ser adicionados sem alterar o servico (aberto/fechado)
- [ ] Endpoint `POST /pedidos/total` retorna subtotal, desconto, frete e total

> Implementada na aula 16 com os padroes Strategy, Factory e Decorator.

---

## US-004 - Acesso autenticado a API (adicional)

**Como** administrador, **eu quero** que apenas usuarios autenticados acessem a API e que somente administradores alterem o catalogo **para** proteger os dados contra alteracoes indevidas.

- **Prioridade:** Must have
- **Labels sugeridas:** `must-have`, `security`

**Criterios de aceitacao**
- [ ] `POST /auth/login` com credenciais validas retorna um access token JWT e um refresh token
- [ ] Requisicoes a `/api/**` sem token retornam `401`
- [ ] Usuario com papel `USER` consegue listar, mas recebe `403` ao tentar criar/alterar/remover
- [ ] `POST /auth/refresh` gera novo access token a partir de um refresh token valido
- [ ] Nenhum segredo (chave JWT, senha) fica hardcoded no codigo

> Implementada na aula 18.

---

## Sugestao de issues para o GitHub Projects

Board com colunas **Backlog | To Do | In Progress | Done**. Criar uma issue por User Story:

| Issue | Titulo | Labels | Coluna inicial |
|-------|--------|--------|----------------|
| #1 | US-001 - Busca de produtos por nome | `must-have`, `backend`, `sprint-1` | Done (aula 14) |
| #2 | US-002 - Validacao de campos obrigatorios | `must-have`, `backend`, `qualidade` | To Do |
| #3 | US-003 - Total do pedido com desconto por tipo de cliente | `should-have`, `backend`, `refactor` | Backlog |
| #4 | US-004 - Acesso autenticado a API (JWT) | `must-have`, `security` | Backlog |

Comandos `gh` sugeridos (a executar pelo dono do repositorio):

```bash
gh label create must-have --color B60205
gh label create should-have --color FBCA04
gh label create backend --color 1D76DB
gh label create sprint-1 --color 0E8A16
gh issue create --title "US-001 - Busca de produtos por nome" --label must-have,backend,sprint-1 \
  --body-file projeto-final/docs/aula-14/user-stories.md
```

Branch e commit vinculados (fluxo da aula): `feature/us-001-busca-produtos`,
mensagem `Implementa US-001: Busca de produtos por nome (closes #1)`.
