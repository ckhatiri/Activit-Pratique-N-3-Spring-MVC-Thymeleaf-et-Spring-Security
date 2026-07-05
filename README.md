# TP3 – Application Web JEE : Gestion des produits
### Spring MVC · Spring Data JPA · Hibernate · Thymeleaf · Spring Security

## Lancement

```bash
mvn spring-boot:run
```

Puis ouvrir : http://localhost:8080

Comptes de démo créés automatiquement au démarrage (`ProduitsWebApplication.start()`) :

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `admin` | `1234` | ADMIN + USER |
| `user`  | `1234` | USER |

## Correspondance avec les points de l'énoncé

**Point 1 — Dépendances Spring Initializr**
`pom.xml` : Spring Web, Spring Data JPA, H2, MySQL, Thymeleaf, Lombok, Spring Security,
Spring Validation, + `thymeleaf-layout-dialect` et `thymeleaf-extras-springsecurity5`
(nécessaires pour le layout et les balises `sec:` dans les vues).

**Point 2 — Entité Product**
`entities/Product.java`, avec annotations `@NotEmpty` / `@Min` / `@DecimalMin` pour la validation.

**Point 3 — ProductRepository**
`repositories/ProductRepository.java` (Spring Data JPA), avec une méthode de recherche paginée.

**Point 4 — Tester la couche DAO**
- Test JUnit isolé : `src/test/java/.../ProductRepositoryTests.java` (`@DataJpaTest`)
- Test "en live" : le `CommandLineRunner` de `ProduitsWebApplication` insère des produits
  au démarrage et les affiche dans la console.

**Point 5 — Désactiver la protection par défaut**
Voir le commentaire en tête de `security/SecurityConfig.java` : pour reproduire cette étape
intermédiaire (utile pour tester les vues MVC avant de sécuriser), remplacer temporairement
le contenu de la méthode `filterChain()` par :
```java
http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).csrf().disable();
return http.build();
```
Le fichier livré contient directement la configuration **finale** et sécurisée (point 7).

**Point 6 — Contrôleur + vues Thymeleaf**
- `web/ProductController.java` : liste (`GET /user/products`), suppression
  (`GET /admin/deleteProduct`), formulaire d'ajout (`GET /admin/formProducts`),
  enregistrement avec validation (`POST /admin/saveProduct`).
- `templates/template.html` : layout Bootstrap (via `thymeleaf-layout-dialect`),
  décoré par `products.html` et `formProducts.html` (`layout:decorate="~{template}"`).
- La validation du formulaire utilise `@Valid` + `BindingResult` côté contrôleur et
  `th:errors` côté vue.

**Point 7 — Sécuriser l'application**
- `security/SecurityConfig.java` : accès public à `/login` et aux ressources statiques,
  `/user/**` réservé à USER et ADMIN, `/admin/**` réservé à ADMIN.
- `security/CustomUserDetailsService.java` : charge les utilisateurs et leurs rôles
  depuis la base (`AppUser` / `Role`, relation ManyToMany).
- `templates/login.html` : page de connexion personnalisée.
- Mots de passe encodés avec `BCryptPasswordEncoder`.

**Point 8 — Fonctionnalités supplémentaires**
- Recherche de produits par nom (barre de recherche sur la liste).
- Pagination de la liste des produits.
- Édition / mise à jour d'un produit (`GET /admin/editProduct`, réutilise le même
  formulaire et la même méthode `saveProduct` que l'ajout).
- Page d'erreur 403 personnalisée en cas d'accès refusé.
- Affichage conditionnel des boutons d'administration selon le rôle (`sec:authorize`
  dans les vues).

## Migration H2 → MySQL

1. `CREATE DATABASE produits_db;`
2. `mvn spring-boot:run -Dspring-boot.run.profiles=mysql`
   (charge `application-mysql.properties` à la place d'H2)

## Journal des commits

*(à compléter au fil de la séance : un commit toutes les ~30 min)*

| Contenu du commit |
|---|
| Setup projet (pom.xml, dépendances) |
| Entité Product + validation |
| ProductRepository + test DAO |
| Contrôleur + vue liste des produits (sans sécurité) |
| Layout Thymeleaf + Bootstrap |
| Formulaire d'ajout avec validation |
| Entités AppUser / Role + CustomUserDetailsService |
| SecurityConfig (rôles, page de login, logout) |
| Recherche + pagination + édition de produit |
| Page 403 + finitions |

## Limites connues

- Les identifiants de démo (`admin/1234`, `user/1234`) sont créés en clair dans le code à
  but pédagogique ; à ne jamais faire en production (utiliser un script d'initialisation
  séparé, des variables d'environnement, etc.).
- La suppression d'un produit se fait via une requête GET (lien cliquable) pour rester
  simple, ce qui n'est pas la meilleure pratique REST (une suppression devrait être un
  DELETE ou un POST). Amélioration possible : formulaire caché + bouton POST.
- Pas de gestion des erreurs de conversion sur les champs numériques vides (le message
  affiché reste générique si `price`/`quantity` sont laissés vides).
