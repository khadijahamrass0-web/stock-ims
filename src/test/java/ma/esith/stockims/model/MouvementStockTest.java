package ma.esith.stockims.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MouvementStockTest {

    private final Article article = new Article("TIS-001", "Rouleau de coton");
    private final LocalDate date = LocalDate.of(2026, 11, 13);

    @Test
    void uneEntreeAUnEffetPositif() {
        Entree entree = new Entree(article, 20, date);
        assertEquals(20, entree.quantiteSignee());
    }

    @Test
    void uneSortieAUnEffetNegatif() {
        Sortie sortie = new Sortie(article, 7, date);
        assertEquals(-7, sortie.quantiteSignee());
    }

    @Test
    void refuseUneQuantiteNulleOuNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Entree(article, 0, date));
        assertThrows(IllegalArgumentException.class,
                () -> new Sortie(article, -5, date));
    }

    @Test
    void refuseUnArticleOuUneDateAbsents() {
        assertThrows(IllegalArgumentException.class,
                () -> new Entree(null, 5, date));
        assertThrows(IllegalArgumentException.class,
                () -> new Entree(article, 5, null));
    }
}
