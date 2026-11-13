package ma.esith.stockims.service;

import ma.esith.stockims.model.Article;

/** Ligne affichée dans la liste : un article et son stock calculé. */
public record EtatStock(Article article, long stock) {

    public Article getArticle() {
        return article;
    }

    public long getStock() {
        return stock;
    }
}
