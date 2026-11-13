package ma.esith.stockims.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Un article du catalogue StockIMS.
 *
 * <p>Les champs sont privés et aucune méthode de modification n'est exposée :
 * un article est créé par son constructeur métier, qui vérifie les données.
 * JPA utilise le constructeur vide protégé pour reconstruire les lignes lues
 * dans MySQL.</p>
 */
@Entity
@Table(name = "article")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @Column(nullable = false, length = 80)
    private String designation;

    public Article(String reference, String designation) {
        String referenceNettoyee = reference == null ? "" : reference.strip();
        String designationNettoyee = designation == null ? "" : designation.strip();

        if (referenceNettoyee.isEmpty()) {
            throw new IllegalArgumentException("La référence est obligatoire.");
        }
        if (referenceNettoyee.length() > 20) {
            throw new IllegalArgumentException("La référence ne peut pas dépasser 20 caractères.");
        }
        if (designationNettoyee.isEmpty()) {
            throw new IllegalArgumentException("La désignation est obligatoire.");
        }
        if (designationNettoyee.length() > 80) {
            throw new IllegalArgumentException("La désignation ne peut pas dépasser 80 caractères.");
        }

        this.reference = referenceNettoyee;
        this.designation = designationNettoyee;
    }
}
