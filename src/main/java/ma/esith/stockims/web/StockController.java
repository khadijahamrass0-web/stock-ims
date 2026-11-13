package ma.esith.stockims.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.esith.stockims.model.Article;
import ma.esith.stockims.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @GetMapping("/{id}")
    public String fiche(@PathVariable("id") Long id, Model model) {
        model.addAttribute("mouvement", new MouvementForm());
        return preparer(id, model);
    }

    @PostMapping("/{id}/entrees")
    public String entree(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("mouvement") MouvementForm form,
                         BindingResult erreurs,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (erreurs.hasErrors()) {
            return preparer(id, model);
        }

        try {
            service.entrer(id, form.getQuantite());
        } catch (IllegalArgumentException e) {
            erreurs.reject("metier", e.getMessage());
            return preparer(id, model);
        }

        redirectAttributes.addFlashAttribute("succes",
                "Entrée de " + form.getQuantite()
                        + " unité(s) enregistrée. Nouveau stock : "
                        + service.calculerStock(id) + ".");
        return "redirect:/stock/" + id;
    }

    @PostMapping("/{id}/sorties")
    public String sortie(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("mouvement") MouvementForm form,
                         BindingResult erreurs,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (erreurs.hasErrors()) {
            return preparer(id, model);
        }

        try {
            service.sortir(id, form.getQuantite());
        } catch (IllegalArgumentException e) {
            erreurs.reject("metier", e.getMessage());
            return preparer(id, model);
        }

        redirectAttributes.addFlashAttribute("succes",
                "Sortie de " + form.getQuantite()
                        + " unité(s) enregistrée. Nouveau stock : "
                        + service.calculerStock(id) + ".");
        return "redirect:/stock/" + id;
    }

    private String preparer(Long id, Model model) {
        Article article = service.chercher(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable."));

        model.addAttribute("article", article);
        model.addAttribute("stock", service.calculerStock(id));
        model.addAttribute("historique", service.historique(id));

        return "stock/fiche";
    }
}
