package com.example.projeto.web;

import com.example.projeto.domain.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interface web server-side (Thymeleaf) de produtos.
 * <p>
 * Nome {@code ProdutoWebController} (o slide usa {@code ProdutoController}): ja existe
 * um bean {@code produtoController} na API REST e dois beans com o mesmo nome
 * impedem a aplicacao de subir ({@code ConflictingBeanDefinitionException}).
 */
@Controller
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoWebController {

    static final String VIEW_LISTA = "produtos/lista";
    static final String VIEW_FORM = "produtos/form";

    private final ProdutoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", service.listarTodos());
        return VIEW_LISTA;
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        model.addAttribute("form", new ProdutoForm());
        return VIEW_FORM;
    }

    /** PRG (Post/Redirect/Get): apos salvar redireciona, evitando reenvio com F5. */
    @PostMapping("/cadastrar")
    @PreAuthorize("hasRole('ADMIN')")
    public String cadastrar(@Valid @ModelAttribute("form") ProdutoForm form, BindingResult result) {
        if (result.hasErrors()) {
            return VIEW_FORM;
        }
        service.cadastrar(form.toEntity());
        return "redirect:/produtos";
    }
}
