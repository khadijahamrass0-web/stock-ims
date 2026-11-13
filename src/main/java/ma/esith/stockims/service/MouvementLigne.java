package ma.esith.stockims.service;

/**
 * Ligne d'historique prête à être affichée.
 *
 * @param quantite quantité saisie, toujours positive
 * @param effet    effet signé sur le stock (+ pour une entrée, − pour une sortie)
 */
public record MouvementLigne(
        Long id,
        String type,
        boolean entree,
        int quantite,
        String date,
        int effet) {

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isEntree() {
        return entree;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getDate() {
        return date;
    }

    public int getEffet() {
        return effet;
    }

    /** Effet formaté pour l'affichage : « +20 » ou « -7 ». */
    public String getEffetAffiche() {
        return effet > 0 ? "+" + effet : String.valueOf(effet);
    }
}
