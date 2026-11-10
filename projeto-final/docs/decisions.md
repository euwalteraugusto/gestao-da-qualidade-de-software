# Registro de Decisoes de Projeto (ADR simplificado)

Formato pedido na aula 16 (`docs/decisions.md`). Cada decisao registra padrao, motivo e arquivos.

---

## Decisao 2026-11-10 (aula 16) - Strategy para descontos

**Padrao:** Strategy
**Motivo:** remover o `switch (tipoCliente)` do `PedidoService` legado (code smells *Long Method* e
*Switch Statement*; violava o principio aberto/fechado - cada novo tipo de cliente exigia editar o servico).
**Arquivos alterados:**
- `domain/pedido/PedidoService.java`
- `domain/pedido/desconto/PoliticaDesconto.java` (interface Strategy)
- `domain/pedido/desconto/DescontoPercentual.java`, `DescontoVIP.java`, `DescontoPremium.java`, `SemDesconto.java`

## Decisao 2026-11-10 (aula 16) - Factory para selecionar a estrategia

**Padrao:** Factory (selecao/registro de estrategias)
**Motivo:** quem chama nao deve conhecer as classes concretas de desconto. A
`PoliticaDescontoFactory` recebe todas as estrategias do Spring e mapeia `TipoCliente -> PoliticaDesconto`;
valida na subida que todo tipo tem exatamente uma estrategia (falha rapida). Alternativa considerada:
`@Conditional`/`@ConditionalOnProperty` (dica do slide) - adequada para escolher **uma** estrategia
por ambiente, mas aqui a escolha depende do cliente **em cada requisicao**, por isso a factory.
**Arquivos:** `domain/pedido/desconto/PoliticaDescontoFactory.java`

## Decisao 2026-11-10 (aula 16) - Decorator para logging do frete

**Padrao:** Decorator (tarefa pos-aula: "Decorator para adicionar logging")
**Motivo:** adicionar log e medicao de tempo ao calculo de frete sem alterar `FreteFixo` nem o
`PedidoService`. A composicao e feita em `config/FreteConfig.java`.
**Arquivos:** `domain/pedido/frete/CalculadoraFrete.java`, `FreteFixo.java`, `CalculadoraFreteComLog.java`, `config/FreteConfig.java`

## Decisao 2026-11-10 (aula 16) - Value Objects e Move Method

**Padroes/refatoracoes:** Value Object (`record`), Move Method, Replace Magic Number with Configuration
**Motivo:** *Primitive Obsession* (`String tipoCliente` -> `enum TipoCliente`), *Feature Envy*
(o servico calculava `preco * quantidade` de cada item -> `ItemPedido#subtotal()`), numeros magicos
`100` e `15` -> propriedades `app.frete.*`.
**Arquivos:** `domain/pedido/TipoCliente.java`, `ItemPedido.java`, `Pedido.java`, `ResumoPedido.java`
