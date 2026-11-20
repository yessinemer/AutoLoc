package tn.esprit.autoloc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.dto.VehiculeInputDTO;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    @Mapping(source = "agence.nom", target = "nomAgence")
    @Mapping(source = "equipements", target = "libellesEquipements")
    VehiculeDTO toDto(Vehicule vehicule);

    @Mapping(target = "idVehicule", ignore = true)
    @Mapping(target = "agence", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    @Mapping(target = "maintenances", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    Vehicule toEntity(VehiculeInputDTO input);

    default Set<String> mapEquipements(Set<Equipement> equipements) {
        if (equipements == null) {
            return Collections.emptySet();
        }
        return equipements.stream()
                .map(Equipement::getLibelle)
                .collect(Collectors.toSet());
    }
}
