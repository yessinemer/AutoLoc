package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.EmployeService;

/**
 * Contrôleur REST pour les employés.
 * <p>
 * Expose en plus des CRUD standards les opérations de mutation RH :
 * <ul>
 *   <li>{@code PUT /api/employes/{id}/desaffecter} — retire l'employé de son agence</li>
 *   <li>{@code PUT /api/employes/{id}/affecter/{idAgence}} — transfère vers une nouvelle agence</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/employes")
public class EmployeController extends AbstractCrudController<Employe> {

    private final EmployeService employeService;

    public EmployeController(EmployeService employeService) {
        super(employeService);
        this.employeService = employeService;
    }

    // ------------------------------------------------------------------ //
    //  Scénario 1 — @ManyToOne : mutation RH
    // ------------------------------------------------------------------ //

    /**
     * Désaffecte un employé de son agence courante.
     * <p>
     * Met la FK {@code agence_id} à NULL dans la table {@code employe}.
     * À appeler avant une réaffectation vers une nouvelle agence.
     *
     * @param id identifiant de l'employé
     */
    @PutMapping("/{id}/desaffecter")
    @Operation(summary = "Désaffecter un employé de son agence")
    public ResponseEntity<Employe> desaffecter(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.desaffecterEmploye(id));
    }

    /**
     * Affecte un employé à une agence cible.
     * <p>
     * Met à jour la FK {@code agence_id} dans la table {@code employe}.
     * Peut s'appeler directement si l'employé n'a pas encore d'agence,
     * ou après un appel à {@code /desaffecter} lors d'une mutation.
     *
     * @param id       identifiant de l'employé
     * @param idAgence identifiant de l'agence cible
     */
    @PutMapping("/{id}/affecter/{idAgence}")
    @Operation(summary = "Affecter un employé à une agence")
    public ResponseEntity<Employe> affecter(@PathVariable Long id,
                                             @PathVariable Long idAgence) {
        return ResponseEntity.ok(employeService.affecterEmploye(id, idAgence));
    }
}
