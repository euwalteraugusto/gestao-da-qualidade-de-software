# language: pt
Funcionalidade: Carrinho de compras
  Como cliente da loja
  Eu quero montar um carrinho com produtos
  Para ver o total da minha compra antes de pagar

  Contexto:
    Dado que tenho um carrinho vazio

  Cenário: Adicionar produtos e calcular o total
    Quando eu adiciono 2 unidades de "Caneta" a R$ 3,50
    E eu adiciono 1 unidade de "Caderno" a R$ 20,00
    Então o total do carrinho deve ser R$ 27,00
    E o carrinho deve ter 3 itens

  Cenário: Aplicar cupom de desconto válido
    Quando eu adiciono 1 unidade de "Mochila" a R$ 100,00
    E eu aplico o cupom "DESCONTO10"
    Então o total do carrinho deve ser R$ 90,00

  Cenário: Cupom inválido é recusado e o total não muda
    Quando eu adiciono 1 unidade de "Mochila" a R$ 100,00
    E eu aplico o cupom "INVALIDO"
    Então o sistema deve recusar o cupom com a mensagem "Cupom inválido: INVALIDO"
    E o total do carrinho deve ser R$ 100,00

  Cenário: Carrinho vazio custa zero
    Então o total do carrinho deve ser R$ 0,00
    E o carrinho deve ter 0 itens

  Cenário: Quantidade zero não é aceita
    Quando eu adiciono 0 unidades de "Caneta" a R$ 3,50
    Então o sistema deve recusar o item com a mensagem "Quantidade deve ser maior que zero"
    E o carrinho deve ter 0 itens

  Esquema do Cenário: Total proporcional à quantidade
    Quando eu adiciono <quantidade> unidades de "Café" a R$ 5,00
    Então o total do carrinho deve ser R$ <total>

    Exemplos:
      | quantidade | total |
      | 1          | 5,00  |
      | 3          | 15,00 |
      | 10         | 50,00 |
