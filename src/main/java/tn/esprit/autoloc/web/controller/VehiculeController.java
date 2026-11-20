package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.mapper.VehiculeMapper;
import tn.esprit.autoloc.service.VehiculeService;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.dto.VehiculeInputDTO;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeController(VehiculeService vehiculeService, VehiculeMapper vehiculeMapper) {
        this.vehiculeService = vehiculeService;
        this.vehiculeMapper = vehiculeMapper;
    }

    @GetMapping
    @Operation(summary = "Lister les véhicules")
    public List<VehiculeDTO> findAll() {
        return vehiculeService.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un véhicule")
    public VehiculeDTO findById(@PathVariable Long id) {
        return vehiculeMapper.toDto(vehiculeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un véhicule")
    public ResponseEntity<VehiculeDTO> create(@RequestBody VehiculeInputDTO input) {
        Vehicule created = vehiculeService.ajouter(vehiculeMapper.toEntity(input));
        return ResponseEntity.ok(vehiculeMapper.toDto(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un véhicule")
    public VehiculeDTO update(@PathVariable Long id, @RequestBody VehiculeInputDTO input) {
        Vehicule updated = vehiculeService.modifier(id, vehiculeMapper.toEntity(input));
        return vehiculeMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehiculeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/affecter-agence/{idAgence}")
    @Operation(summary = "Rattacher un véhicule à une agence (@ManyToOne)")
    public ResponseEntity<VehiculeDTO> affecterAgence(@PathVariable Long id,
                                                       @PathVariable Long idAgence) {
        Vehicule updated = vehiculeService.affecterVehicule(id, idAgence);
        return ResponseEntity.ok(vehiculeMapper.toDto(updated));
    }

    @PutMapping("/{id}/equipements/{idEquipement}")
    @Operation(summary = "Installer un équipement sur un véhicule (@ManyToMany)")
    public ResponseEntity<VehiculeDTO> affecterEquipement(@PathVariable Long id,
                                                           @PathVariable Long idEquipement) {
        Vehicule updated = vehiculeService.affecterEquipement(id, idEquipement);
        return ResponseEntity.ok(vehiculeMapper.toDto(updated));
    }

    @DeleteMapping("/{id}/equipements/{idEquipement}")
    @Operation(summary = "Retirer un équipement d'un véhicule (@ManyToMany)")
    public ResponseEntity<VehiculeDTO> desaffecterEquipement(@PathVariable Long id,
                                                              @PathVariable Long idEquipement) {
        Vehicule updated = vehiculeService.desaffecterEquipement(id, idEquipement);
        return ResponseEntity.ok(vehiculeMapper.toDto(updated));
    }
}
