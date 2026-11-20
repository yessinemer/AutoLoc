package tn.esprit.autoloc.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.dto.VehiculeInputDTO;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VehiculeMapperTest {

    private final VehiculeMapper mapper = Mappers.getMapper(VehiculeMapper.class);

    @Test
    void mapsAgencyAndEquipmentToSimpleValues() {
        Vehicule vehicule = new Vehicule();
        Agence agence = new Agence();
        agence.setNom("Agence de Tunis");
        Equipement equipement = new Equipement();
        equipement.setLibelle("Siège bébé");
        vehicule.setAgence(agence);
        vehicule.setEquipements(Set.of(equipement));

        VehiculeDTO dto = mapper.toDto(vehicule);

        assertEquals("Agence de Tunis", dto.getNomAgence());
        assertEquals(Set.of("Siège bébé"), dto.getLibellesEquipements());
    }

    @Test
    void mapsInputFieldsWithoutAnIdOrRelationships() {
        VehiculeInputDTO input = new VehiculeInputDTO();
        input.setImmatriculation("123-TUN-456");
        input.setMarque("Renault");
        input.setModele("Clio");
        input.setCategorie(CategorieVehicule.CITADINE);
        input.setTarifJournalier(new BigDecimal("125.00"));
        input.setStatut(StatutVehicule.DISPONIBLE);

        Vehicule vehicule = mapper.toEntity(input);

        assertNull(vehicule.getIdVehicule());
        assertNull(vehicule.getAgence());
        assertEquals(Set.of(), vehicule.getEquipements());
        assertEquals(input.getImmatriculation(), vehicule.getImmatriculation());
        assertEquals(input.getTarifJournalier(), vehicule.getTarifJournalier());
    }
}
