package ma.esith.stockims.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ArticleTest {

    @Test
    void creeUnArticleEtNettoieLesEspaces() {
        Article article = new Article("  TIS-001  ", "  Rouleau de coton  ");

        assertEquals("TIS-001", article.getReference());
        assertEquals("Rouleau de coton", article.getDesignation());
    }

    @Test
    void refuseUneReferenceVide() {
        assertThrows(IllegalArgumentException.class,
                () -> new Article("   ", "Rouleau de coton"));
    }

    @Test
    void refuseUneDesignationVide() {
        assertThrows(IllegalArgumentException.class,
                () -> new Article("TIS-001", " "));
    }

    @Test
    void refuseUneReferenceTropLongue() {
        assertThrows(IllegalArgumentException.class,
                () -> new Article("123456789012345678901", "Rouleau de coton"));
    }
}
