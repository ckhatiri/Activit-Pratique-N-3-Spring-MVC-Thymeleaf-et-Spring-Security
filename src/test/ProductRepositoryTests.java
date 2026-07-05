package ma.projet.produits.repositories;

import ma.projet.produits.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Point 4 : Test de la couche DAO.
 * @DataJpaTest configure automatiquement une base H2 en mémoire, isolée de
 * l'application réelle, et injecte le repository à tester.
 */
@DataJpaTest
class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testAjouterEtConsulter() {
        Product p = productRepository.save(Product.builder()
                .name("Imprimante laser")
                .price(950.0)
                .quantity(15)
                .build());

        assertTrue(p.getId() > 0);
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testRecherche() {
        productRepository.save(Product.builder().name("Clavier AZERTY").price(200).quantity(10).build());
        productRepository.save(Product.builder().name("Souris optique").price(80).quantity(30).build());

        long count = productRepository
                .findByNameContainsIgnoreCase("clavier", PageRequest.of(0, 10))
                .getTotalElements();

        assertEquals(1, count);
    }
}
