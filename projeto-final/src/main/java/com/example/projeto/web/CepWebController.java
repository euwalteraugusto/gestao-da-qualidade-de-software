package com.example.projeto.web;

import com.example.projeto.integration.CepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Pagina de consulta de CEP (integracao com API externa ViaCEP).
 */
@Controller
@RequiredArgsConstructor
public class CepWebController {

    private final CepService cepService;

    @GetMapping("/cep")
    public String consultar(@RequestParam(required = false) String cep, Model model) {
        if (cep != null && !cep.isBlank()) {
            model.addAttribute("cep", cep);
            model.addAttribute("endereco", cepService.buscarPorCep(cep));
        }
        return "cep";
    }
}
