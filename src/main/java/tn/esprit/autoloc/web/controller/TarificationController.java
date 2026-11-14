package tn.esprit.autoloc.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.VehiculeService;
import tn.esprit.autoloc.tarification.TarificationService;

import java.util.Map;
import java.util.Set;

/**
 * Contrôleur REST exposant les calculs tarifaires — Partie C.
 * <p>
 * Endpoints disponibles :
 * <ul>
 *   <li>{@code GET /api/tarification?type=STANDARD&vehiculeId=1&jours=3}</li>
 *   <li>{@code GET /api/tarification/types} — liste les stratégies disponibles</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/tarification")
public class TarificationController {

    private final TarificationService tarificationService;
    private final VehiculeService vehiculeService;

    public TarificationController(TarificationService tarificationService,
                                   VehiculeService vehiculeService) {
        this.tarificationService = tarificationService;
        this.vehiculeService     = vehiculeService;
    }

    /**
     * Calcule le montant total d'une location.
     *
     * @param type       type de tarification ({@code STANDARD}, {@code WEEKEND}, {@code LONGUE_DUREE}, {@code ENTREPRISE})
     * @param vehiculeId identifiant du véhicule
     * @param jours      nombre de jours de location
     * @return JSON avec le détail du calcul
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> calculer(
            @RequestParam String type,
            @RequestParam Long vehiculeId,
            @RequestParam int jours) {

        Vehicule vehicule = vehiculeService.findById(vehiculeId);
        double montant = tarificationService.calculer(type, vehicule, jours);

        Map<String, Object> result = Map.of(
                "vehiculeId",        vehiculeId,
                "immatriculation",   vehicule.getImmatriculation(),
                "tarifJournalier",   vehicule.getTarifJournalier(),
                "jours",             jours,
                "typeStrategie",     type.toUpperCase(),
                "montantTotal",      montant
        );
        return ResponseEntity.ok(result);
    }

    /**
     * Retourne la liste des types de tarification disponibles dans le contexte Spring.
     */
    @GetMapping("/types")
    public ResponseEntity<Set<String>> getTypesDisponibles() {
        return ResponseEntity.ok(tarificationService.getTypesDisponibles());
    }
}
