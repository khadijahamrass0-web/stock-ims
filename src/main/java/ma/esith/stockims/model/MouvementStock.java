package ma.esith.stockims.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Données communes aux entrées et aux sorties.
 * Classe mère mappée : ses colonnes sont recopiées dans les tables
 * entree et sortie.
 */
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private LocalDate dateMouvement;

    protected MouvementStock(Article article, int quantite, LocalDate dateMouvement) {
        if (article == null) {
            throw new IllegalArgumentException("L'article est obligatoire.");
        }
        if (dateMouvement == null) {
            throw new IllegalArgumentException("La date du mouvement est obligatoire.");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être un entier strictement positif.");
        }
        this.article = article;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
    }

    /**
     * Effet du mouvement sur le stock : positif pour une entrée,
     * négatif pour une sortie.
     */
    public abstract int quantiteSignee();
}
