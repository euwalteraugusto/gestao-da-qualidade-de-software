# language: pt
Funcionalidade: Autenticação de usuário
  Como administrador do sistema
  Eu quero que apenas usuários com a senha correta entrem
  Para proteger os dados da aplicação

  Cenário: Login válido
    Dado que o usuário "admin" com senha "1234" está cadastrado
    Quando eu faço login com "admin" e "1234"
    Então o acesso é concedido

  Cenário: Senha inválida
    Dado que o usuário "admin" com senha "1234" está cadastrado
    Quando eu faço login com "admin" e "senhaerrada"
    Então o acesso é negado

  Cenário: Usuário inexistente
    Dado que o usuário "admin" com senha "1234" está cadastrado
    Quando eu faço login com "fantasma" e "1234"
    Então o acesso é negado

  Cenário: Conta bloqueada após 3 senhas erradas seguidas
    Dado que o usuário "admin" com senha "1234" está cadastrado
    Quando eu erro a senha de "admin" 3 vezes seguidas
    Então a conta é bloqueada
    Mas ao tentar entrar com a senha correta "1234" o acesso continua negado

  Cenário: Acerto antes do limite zera a contagem de erros
    Dado que o usuário "admin" com senha "1234" está cadastrado
    Quando eu erro a senha de "admin" 2 vezes seguidas
    E eu faço login com "admin" e "1234"
    Então o acesso é concedido
    Quando eu erro a senha de "admin" 2 vezes seguidas
    Então a conta não está bloqueada
