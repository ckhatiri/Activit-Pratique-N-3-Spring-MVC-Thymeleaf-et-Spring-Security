package ma.projet.produits.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Point 2 : Entité JPA Product.
 * Les annotations javax.validation (@NotEmpty, @Min, @DecimalMin) sont utilisées
 * par Spring Validation lors de la soumission du formulaire (point 6).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Le nom du produit est obligatoire")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif")
    private double price;

    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private int quantity;
}
