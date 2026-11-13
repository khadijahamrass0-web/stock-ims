package ma.esith.stockims.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.esith.stockims.model.Article;
import ma.esith.stockims.repository.ArticleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Règles de gestion du catalogue des articles. */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository repository;

    @Transactional(readOnly = true)
    public List<Article> lister() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "reference"));
    }

    /**
     * Crée un article après vérification de la référence unique.
     * Le constructeur d'{@link Article} vérifie les autres règles métier.
     */
    @Transactional
    public Article creer(String reference, String designation) {
        String referenceNettoyee = reference == null ? "" : reference.strip();

        if (!referenceNettoyee.isEmpty()
                && repository.existsByReferenceIgnoreCase(referenceNettoyee)) {
            throw new IllegalArgumentException("Cette référence existe déjà.");
        }

        try {
            return repository.saveAndFlush(new Article(reference, designation));
        } catch (DataIntegrityViolationException e) {
            // Filet de sécurité : la contrainte unique de MySQL reste l'arbitre final.
            throw new IllegalArgumentException("Cette référence existe déjà.", e);
        }
    }
}
