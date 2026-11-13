package ma.esith.stockims.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Une entrée augmente le stock de l'article. */
@Entity
@Table(name = "entree")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Entree extends MouvementStock {

    public Entree(Article article, int quantite, LocalDate date) {
        super(article, quantite, date);
    }

    @Override
    public int quantiteSignee() {
        return getQuantite();
    }
}
