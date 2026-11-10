package br.gqs.aula03.antes;

import br.gqs.aula03.modelo.Cliente;
import java.util.List;

// VERSAO "ANTES" (codigo ruim mantido de proposito para o relatorio antes/depois).
// Problemas: nomes curtos (c, cli), if aninhado, laco manual, System.out direto,
// regra de maioridade errada (> 18 exclui quem tem exatamente 18 anos).
public class Relatorio {
    public void gerar(List<Cliente> c) {
        if(c != null) {
            for(Cliente cli : c) {
                if(cli.getIdade() > 18) {
                    System.out.println(cli.getNome());
                }
            }
        }
    }
}
