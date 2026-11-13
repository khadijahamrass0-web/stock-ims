package ma.esith.stockims.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import ma.esith.stockims.model.Article;
import ma.esith.stockims.model.Entree;
import ma.esith.stockims.model.MouvementStock;
import ma.esith.stockims.model.Sortie;
import ma.esith.stockims.repository.ArticleRepository;
import ma.esith.stockims.repository.EntreeRepository;
import ma.esith.stockims.repository.SortieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Toutes les règles du stock : calcul, enregistrement des mouvements et
 * refus des sorties supérieures au stock disponible.
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE);

    private final ArticleRepository articles;
    private final EntreeRepository entrees;
    private final SortieRepository sorties;

    /**
     * stock = somme des entrées − somme des sorties.
     * La liste commune de {@link MouvementStock} fait travailler le
     * polymorphisme de {@code quantiteSignee()}.
     */
    @Transactional(readOnly = true)
    public long calculerStock(Long articleId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        mouvements.addAll(entrees.findByArticle_Id(articleId));
        mouvements.addAll(sorties.findByArticle_Id(articleId));

        long stock = 0;
        for (MouvementStock mouvement : mouvements) {
            stock += mouvement.quantiteSignee();
        }
        return stock;
    }

    @Transactional(readOnly = true)
    public Optional<Article> chercher(Long id) {
        return articles.findById(id);
    }

    /** Prépare la liste des articles avec leur stock, triée par référence. */
    @Transactional(readOnly = true)
    public List<EtatStock> etats() {
        List<EtatStock> resultat = new ArrayList<>();
        for (Article article : articles.findAll(Sort.by(Sort.Direction.ASC, "reference"))) {
            resultat.add(new EtatStock(article, calculerStock(article.getId())));
        }
        return resultat;
    }

    /** Historique des mouvements, du plus ancien au plus récent. */
    @Transactional(readOnly = true)
    public List<MouvementLigne> historique(Long articleId) {
        List<MouvementStock> mouvements = new ArrayList<>();
        mouvements.addAll(entrees.findByArticle_Id(articleId));
        mouvements.addAll(sorties.findByArticle_Id(articleId));
        mouvements.sort(Comparator
                .comparing(MouvementStock::getDateMouvement)
                .thenComparing(MouvementStock::getId));

        List<MouvementLigne> lignes = new ArrayList<>();
        for (MouvementStock mouvement : mouvements) {
            boolean estUneEntree = mouvement instanceof Entree;
            lignes.add(new MouvementLigne(
                    mouvement.getId(),
                    estUneEntree ? "Entrée" : "Sortie",
                    estUneEntree,
                    mouvement.getQuantite(),
                    mouvement.getDateMouvement().format(FORMAT_DATE),
                    mouvement.quantiteSignee()));
        }
        return lignes;
    }

    @Transactional
    public void entrer(Long id, int quantite) {
        Article article = verrouiller(id);
        entrees.save(new Entree(article, quantite, LocalDate.now()));
    }

    @Transactional
    public void sortir(Long id, int quantite) {
        Article article = verrouiller(id);
        long stock = calculerStock(id);

        if (quantite > stock) {
            throw new IllegalArgumentException(
                    "Stock insuffisant : " + stock + " unité(s) disponible(s).");
        }

        sorties.save(new Sortie(article, quantite, LocalDate.now()));
    }

    private Article verrouiller(Long id) {
        return articles.verrouiller(id).orElseThrow(
                () -> new IllegalArgumentException("Article introuvable."));
    }
}
