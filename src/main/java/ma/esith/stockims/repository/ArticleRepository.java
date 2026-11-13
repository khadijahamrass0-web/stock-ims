package ma.esith.stockims.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import ma.esith.stockims.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Accès aux articles. Spring Data JPA génère l'implémentation de cette
 * interface : aucune classe concrète n'est nécessaire.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /** Détecte une référence déjà utilisée, sans tenir compte de la casse. */
    boolean existsByReferenceIgnoreCase(String reference);

    /**
     * Charge un article en posant un verrou exclusif jusqu'à la fin de la
     * transaction : deux sorties simultanées ne peuvent pas utiliser le même
     * stock de départ.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Article a where a.id = :id")
    Optional<Article> verrouiller(@Param("id") Long id);
}
