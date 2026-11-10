package br.edu.iqs.aula06;

/** Servico de validacao de usuario (Atividade 3 da Aula 6). */
public class UsuarioService {

    public static final int TAMANHO_MINIMO_SENHA = 8;

    /** Uma senha e valida se nao for nula e tiver pelo menos 8 caracteres. */
    public boolean validarSenha(String senha) {
        return senha != null && senha.length() >= TAMANHO_MINIMO_SENHA;
    }
}
