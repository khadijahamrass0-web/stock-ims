package ma.esith.stockims.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Une sortie diminue le stock de l'article. */
@Entity
@Table(name = "sortie")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sortie extends MouvementStock {

    public Sortie(Article article, int quantite, LocalDate date) {
        super(article, quantite, date);
    }

    @Override
    public int quantiteSignee() {
        return -getQuantite();
    }
}
