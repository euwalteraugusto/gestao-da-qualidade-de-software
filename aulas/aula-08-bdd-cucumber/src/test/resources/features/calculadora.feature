# language: pt
Funcionalidade: Calculadora
  Como um usuário
  Eu quero somar e dividir números
  Para obter resultados corretos e ser avisado quando a operação for inválida

  Cenário: Soma de números positivos
    Dado que eu tenho os números 2 e 3
    Quando eu somo estes números
    Então o resultado deve ser 5

  Cenário: Soma de dois números
    Dado que eu tenho os números 5 e 7
    Quando eu peço para somar
    Então o resultado deve ser 12

  Cenário: Divisão por zero
    Dado que eu tenho os números 10 e 0
    Quando eu peço para dividir
    Então o sistema deve informar "Divisão por zero não permitida"

  Cenário: Divisão com resultado decimal
    Dado que eu tenho os números 9 e 4
    Quando eu peço para dividir
    Então o resultado deve ser 2,25

  Esquema do Cenário: Somas variadas
    Dado que eu tenho os números <a> e <b>
    Quando eu peço para somar
    Então o resultado deve ser <resultado>

    Exemplos:
      | a  | b  | resultado |
      | 1  | 1  | 2         |
      | -1 | -1 | -2        |
      | 0  | 5  | 5         |
      | 10 | -4 | 6         |

  Esquema do Cenário: Divisões válidas
    Dado que eu tenho os números <a> e <b>
    Quando eu peço para dividir
    Então o resultado deve ser <resultado>
    E nenhum erro deve ter ocorrido

    Exemplos:
      | a | b | resultado |
      | 4 | 2 | 2         |
      | 9 | 4 | 2,25      |
      | 1 | 8 | 0,125     |
