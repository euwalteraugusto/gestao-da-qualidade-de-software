package com.example.projeto.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pagina inicial: redireciona para a listagem de produtos.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/produtos";
    }
}
