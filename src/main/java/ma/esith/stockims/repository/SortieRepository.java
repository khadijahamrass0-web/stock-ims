package ma.esith.stockims.repository;

import java.util.List;
import ma.esith.stockims.model.Sortie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SortieRepository extends JpaRepository<Sortie, Long> {

    List<Sortie> findByArticle_Id(Long articleId);
}
