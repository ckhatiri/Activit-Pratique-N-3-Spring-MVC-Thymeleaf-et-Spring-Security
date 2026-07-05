package ma.projet.produits.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("/")
    public String home() {
        return "redirect:/user/products";
    }

    // Vue fournie pour la page de login personnalisée (déclarée dans SecurityConfig.loginPage("/login"))
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/403")
    public String accesRefuse() {
        return "403";
    }
}
