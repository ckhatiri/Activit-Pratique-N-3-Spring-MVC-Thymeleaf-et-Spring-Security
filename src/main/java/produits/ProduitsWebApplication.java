package ma.projet.produits;

import ma.projet.produits.entities.AppUser;
import ma.projet.produits.entities.Product;
import ma.projet.produits.entities.Role;
import ma.projet.produits.repositories.AppUserRepository;
import ma.projet.produits.repositories.ProductRepository;
import ma.projet.produits.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class ProduitsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProduitsWebApplication.class, args);
    }

    /**
     * Point 4 : Teste la couche DAO au démarrage (ajout + affichage dans les logs),
     * et prépare les données nécessaires à la démo (produits, rôles, utilisateurs).
     */
    @Bean
    CommandLineRunner start(ProductRepository productRepository,
                             RoleRepository roleRepository,
                             AppUserRepository appUserRepository,
                             PasswordEncoder passwordEncoder) {
        return args -> {

            // ---------- Test de la couche DAO : Product ----------
            Stream.of("Ordinateur portable", "Souris sans fil", "Clavier mécanique", "Écran 27 pouces")
                    .forEach(nom -> productRepository.save(Product.builder()
                            .name(nom)
                            .price(100 + Math.random() * 2000)
                            .quantity((int) (Math.random() * 50))
                            .build()));

            System.out.println(">>> [Test DAO] Produits en base :");
            productRepository.findAll().forEach(p -> System.out.println("\t" + p));

            // ---------- Rôles ----------
            Role roleAdmin = roleRepository.save(Role.builder().role("ADMIN").build());
            Role roleUser = roleRepository.save(Role.builder().role("USER").build());

            // ---------- Utilisateurs ----------
            List<Role> rolesAdmin = new ArrayList<>(List.of(roleAdmin, roleUser));
            appUserRepository.save(AppUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("1234"))
                    .enabled(true)
                    .roles(rolesAdmin)
                    .build());

            List<Role> rolesUser = new ArrayList<>(List.of(roleUser));
            appUserRepository.save(AppUser.builder()
                    .username("user")
                    .password(passwordEncoder.encode("1234"))
                    .enabled(true)
                    .roles(rolesUser)
                    .build());

            System.out.println(">>> Comptes créés : admin/1234 (ADMIN+USER) et user/1234 (USER)");
        };
    }
}
