/* ============================================================
   StockIMS — interactions légères
   1. Filtre instantané de la liste des articles
   2. Confirmation avant une sortie
   3. Fermeture des messages de succès
   Les validations métier restent côté serveur.
   ============================================================ */

(() => {
    "use strict";

    /** Minuscules sans accents, pour une recherche tolérante. */
    const normaliser = (texte) =>
        (texte || "")
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase()
            .trim();

    /* ---------- 1. Filtre de la liste ------------------------ */

    const filtre = document.querySelector("[data-filtre-articles]");

    if (filtre) {
        const lignes = Array.from(
            document.querySelectorAll("#tableau-articles tbody tr[data-recherche]")
        );
        const compteur = document.getElementById("compteur-resultats");
        const aucunResultat = document.getElementById("aucun-resultat");
        const effacer = document.getElementById("effacer-filtre");

        const appliquerFiltre = () => {
            const terme = normaliser(filtre.value);
            let visibles = 0;

            lignes.forEach((ligne) => {
                const contenu = normaliser(ligne.dataset.recherche);
                const visible = contenu.includes(terme);
                ligne.hidden = !visible;
                if (visible) {
                    visibles += 1;
                }
            });

            if (compteur) {
                compteur.textContent =
                    visibles + " article" + (visibles > 1 ? "s" : "");
            }
            if (aucunResultat) {
                aucunResultat.hidden = visibles !== 0;
            }
            if (effacer) {
                effacer.hidden = filtre.value.trim() === "";
            }
        };

        filtre.addEventListener("input", appliquerFiltre);

        if (effacer) {
            effacer.addEventListener("click", () => {
                filtre.value = "";
                appliquerFiltre();
                filtre.focus();
            });
        }

        appliquerFiltre();
    }

    /* ---------- 2. Confirmation d'une sortie ------------------ */

    const formulaire = document.getElementById("form-mouvement");
    const dialogue = document.getElementById("dialogue-sortie");

    if (formulaire && dialogue && typeof dialogue.showModal === "function") {
        const champQuantite = formulaire.querySelector("#quantite");
        const texteQuantite = document.getElementById("dialogue-quantite");
        const texteArticle = document.getElementById("dialogue-article");
        const texteStock = document.getElementById("dialogue-stock");
        const texteApres = document.getElementById("dialogue-apres");
        const avertissement = document.getElementById("dialogue-avertissement");
        const boutonAnnuler = document.getElementById("dialogue-annuler");
        const boutonConfirmer = document.getElementById("dialogue-confirmer");

        let boutonEnAttente = null;
        let sortieConfirmee = false;

        const ouvrirDialogue = (submitter) => {
            const quantite = Number.parseInt(champQuantite.value, 10);
            const stock = Number.parseInt(formulaire.dataset.stock || "0", 10);
            const apres = stock - quantite;

            texteQuantite.textContent = String(quantite);
            texteArticle.textContent =
                formulaire.dataset.reference + " — " + formulaire.dataset.designation;
            texteStock.textContent = String(stock);
            texteApres.textContent = String(apres);
            texteApres.classList.toggle("effet--negatif", apres < 0);
            avertissement.hidden = quantite <= stock;

            boutonEnAttente = submitter;
            dialogue.showModal();
            boutonAnnuler.focus();
        };

        formulaire.addEventListener("submit", (evenement) => {
            if (sortieConfirmee) {
                return;
            }

            const submitter = evenement.submitter || boutonEnAttente;
            if (!submitter || submitter.dataset.confirmation !== "sortie") {
                return;
            }

            if (!formulaire.checkValidity()) {
                evenement.preventDefault();
                formulaire.reportValidity();
                return;
            }

            evenement.preventDefault();
            ouvrirDialogue(submitter);
        });

        if (boutonAnnuler) {
            boutonAnnuler.addEventListener("click", () => dialogue.close());
        }

        if (boutonConfirmer) {
            boutonConfirmer.addEventListener("click", () => {
                sortieConfirmee = true;
                dialogue.close();

                if (!boutonEnAttente) {
                    return;
                }
                if (typeof formulaire.requestSubmit === "function") {
                    formulaire.requestSubmit(boutonEnAttente);
                } else {
                    formulaire.submit();
                }
            });
        }

        dialogue.addEventListener("close", () => {
            if (!sortieConfirmee) {
                boutonEnAttente = null;
            }
        });
    }

    /* ---------- 3. Messages de succès ------------------------ */

    const retirerAlerte = (alerte) => {
        alerte.style.opacity = "0";
        alerte.style.transform = "translateY(-4px)";
        window.setTimeout(() => alerte.remove(), 200);
    };

    document.querySelectorAll("[data-auto-dismiss]").forEach((alerte) => {
        const boutonFermer = alerte.querySelector("[data-fermer-alerte]");

        if (boutonFermer) {
            boutonFermer.addEventListener("click", () => retirerAlerte(alerte));
        }
        window.setTimeout(() => {
            if (alerte.isConnected) {
                retirerAlerte(alerte);
            }
        }, 7000);
    });
})();
