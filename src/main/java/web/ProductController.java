package ma.projet.produits.web;

import lombok.AllArgsConstructor;
import ma.projet.produits.entities.Product;
import ma.projet.produits.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    // ---------- Point 6 : afficher la liste des produits ----------
    // ---------- Point 8 : recherche + pagination ----------
    @GetMapping("/user/products")
    public String listeProduits(Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "5") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Product> pageProduits = productRepository.findByNameContainsIgnoreCase(
                keyword, PageRequest.of(page, size));

        model.addAttribute("listProducts", pageProduits.getContent());
        model.addAttribute("pages", new int[pageProduits.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "products";
    }

    // ---------- Point 6 : supprimer un produit ----------
    @GetMapping("/admin/deleteProduct")
    public String supprimerProduit(@RequestParam("id") Long id,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        productRepository.deleteById(id);
        return "redirect:/user/products?page=" + page + "&keyword=" + keyword;
    }

    // ---------- Point 6 : formulaire d'ajout ----------
    @GetMapping("/admin/formProducts")
    public String formulaireNouveauProduit(Model model) {
        model.addAttribute("product", Product.builder().build());
        return "formProducts";
    }

    // ---------- Point 8 : formulaire d'édition (pré-rempli) ----------
    @GetMapping("/admin/editProduct")
    public String formulaireEditionProduit(@RequestParam("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        model.addAttribute("product", product);
        return "formProducts";
    }

    // ---------- Point 6 : enregistrer un produit (ajout ET mise à jour) avec validation ----------
    @PostMapping("/admin/saveProduct")
    public String enregistrerProduit(@Valid Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Retourne directement au formulaire avec les messages d'erreur (th:errors)
            return "formProducts";
        }
        productRepository.save(product);
        return "redirect:/user/products";
    }
}
