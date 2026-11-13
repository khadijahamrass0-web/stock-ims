package ma.esith.stockims.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ma.esith.stockims.model.Article;
import ma.esith.stockims.model.Entree;
import ma.esith.stockims.model.Sortie;
import ma.esith.stockims.repository.ArticleRepository;
import ma.esith.stockims.repository.EntreeRepository;
import ma.esith.stockims.repository.SortieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    private static final Long ARTICLE_ID = 1L;

    @Mock
    private ArticleRepository articles;

    @Mock
    private EntreeRepository entrees;

    @Mock
    private SortieRepository sorties;

    @InjectMocks
    private StockService service;

    private Article article;

    @BeforeEach
    void preparer() {
        article = new Article("TIS-001", "Rouleau de coton");
    }

    @Test
    void calculeLeStockAvecLesEntreesEtLesSorties() {
        when(entrees.findByArticle_Id(ARTICLE_ID))
                .thenReturn(List.of(new Entree(article, 20, LocalDate.now())));
        when(sorties.findByArticle_Id(ARTICLE_ID))
                .thenReturn(List.of(new Sortie(article, 7, LocalDate.now())));

        assertEquals(13, service.calculerStock(ARTICLE_ID));
    }

    @Test
    void unArticleSansMouvementAAUnStockNul() {
        when(entrees.findByArticle_Id(ARTICLE_ID)).thenReturn(List.of());
        when(sorties.findByArticle_Id(ARTICLE_ID)).thenReturn(List.of());

        assertEquals(0, service.calculerStock(ARTICLE_ID));
    }

    @Test
    void refuseUneSortieSuperieureAuStock() {
        when(articles.verrouiller(ARTICLE_ID)).thenReturn(Optional.of(article));
        when(entrees.findByArticle_Id(ARTICLE_ID))
                .thenReturn(List.of(new Entree(article, 20, LocalDate.now())));
        when(sorties.findByArticle_Id(ARTICLE_ID))
                .thenReturn(List.of(new Sortie(article, 7, LocalDate.now())));

        IllegalArgumentException erreur = assertThrows(IllegalArgumentException.class,
                () -> service.sortir(ARTICLE_ID, 15));

        assertTrue(erreur.getMessage().contains("13"));
        verify(sorties, never()).save(any());
    }

    @Test
    void accepteUneSortieEgaleAuStock() {
        when(articles.verrouiller(ARTICLE_ID)).thenReturn(Optional.of(article));
        when(entrees.findByArticle_Id(ARTICLE_ID))
                .thenReturn(List.of(new Entree(article, 20, LocalDate.now())));
        when(sorties.findByArticle_Id(ARTICLE_ID))
                .thenReturn(List.of(new Sortie(article, 7, LocalDate.now())));

        service.sortir(ARTICLE_ID, 13);

        ArgumentCaptor<Sortie> captor = ArgumentCaptor.forClass(Sortie.class);
        verify(sorties).save(captor.capture());
        assertEquals(13, captor.getValue().getQuantite());
    }
}
