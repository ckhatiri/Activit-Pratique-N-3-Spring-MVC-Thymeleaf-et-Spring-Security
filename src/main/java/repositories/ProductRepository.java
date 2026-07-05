package ma.projet.produits.repositories;

import ma.projet.produits.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Point 8 : Recherche de produits (avec pagination, réutilisée par le contrôleur)
    Page<Product> findByNameContainsIgnoreCase(String motCle, Pageable pageable);
}
