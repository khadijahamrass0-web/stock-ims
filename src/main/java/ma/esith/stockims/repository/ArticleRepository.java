package ma.esith.stockims.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import ma.esith.stockims.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Accès aux articles (implémentation fournie par Spring Data JPA).
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /** Vrai si la référence existe déjà, casse ignorée. */
    boolean existsByReferenceIgnoreCase(String reference);

    /**
     * Charge l'article avec un verrou exclusif jusqu'à la fin de la
     * transaction, pour éviter deux sorties simultanées sur le même stock.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Article a where a.id = :id")
    Optional<Article> verrouiller(@Param("id") Long id);
}
