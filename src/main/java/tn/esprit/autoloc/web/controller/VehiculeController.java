package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.VehiculeService;

/**
 * Contrôleur REST pour les véhicules.
 * <p>
 * Expose en plus des CRUD standards les opérations d'affectation :
 * <ul>
 *   <li>{@code PUT /api/vehicules/{id}/affecter-agence/{idAgence}}
 *       — rattache un véhicule à une agence (@ManyToOne)</li>
 *   <li>{@code PUT /api/vehicules/{id}/equipements/{idEquipement}}
 *       — installe un équipement (@ManyToMany)</li>
 *   <li>{@code DELETE /api/vehicules/{id}/equipements/{idEquipement}}
 *       — retire un équipement (@ManyToMany)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController extends AbstractCrudController<Vehicule> {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        super(vehiculeService);
        this.vehiculeService = vehiculeService;
    }

    // ------------------------------------------------------------------ //
    //  Scénario 2 — @ManyToOne : rattachement véhicule → agence
    // ------------------------------------------------------------------ //

    /**
     * Rattache un véhicule à une agence.
     * <p>
     * Met à jour la FK {@code agence_id} dans la table {@code vehicule}.
     * Aucune modification de la table {@code agence}.
     *
     * @param id       identifiant du véhicule
     * @param idAgence identifiant de l'agence cible
     */
    @PutMapping("/{id}/affecter-agence/{idAgence}")
    @Operation(summary = "Rattacher un véhicule à une agence (@ManyToOne)")
    public ResponseEntity<Vehicule> affecterAgence(@PathVariable Long id,
                                                    @PathVariable Long idAgence) {
        return ResponseEntity.ok(vehiculeService.affecterVehicule(id, idAgence));
    }

    // ------------------------------------------------------------------ //
    //  Scénario 3 — @ManyToMany : équipements (siège bébé, GPS…)
    // ------------------------------------------------------------------ //

    /**
     * Installe un équipement sur un véhicule.
     * <p>
     * Insère une ligne dans la table de jointure {@code vehicule_equipement}.
     *
     * @param id           identifiant du véhicule
     * @param idEquipement identifiant de l'équipement à installer
     */
    @PutMapping("/{id}/equipements/{idEquipement}")
    @Operation(summary = "Installer un équipement sur un véhicule (@ManyToMany)")
    public ResponseEntity<Vehicule> affecterEquipement(@PathVariable Long id,
                                                        @PathVariable Long idEquipement) {
        return ResponseEntity.ok(vehiculeService.affecterEquipement(id, idEquipement));
    }

    /**
     * Retire un équipement d'un véhicule.
     * <p>
     * Supprime la ligne dans la table de jointure {@code vehicule_equipement}.
     * L'équipement lui-même n'est pas supprimé.
     *
     * @param id           identifiant du véhicule
     * @param idEquipement identifiant de l'équipement à retirer
     */
    @DeleteMapping("/{id}/equipements/{idEquipement}")
    @Operation(summary = "Retirer un équipement d'un véhicule (@ManyToMany)")
    public ResponseEntity<Vehicule> desaffecterEquipement(@PathVariable Long id,
                                                           @PathVariable Long idEquipement) {
        return ResponseEntity.ok(vehiculeService.desaffecterEquipement(id, idEquipement));
    }
}
