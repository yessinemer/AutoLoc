package tn.esprit.autoloc.service;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;

/**
 * Validateur métier des véhicules — Partie B, SRP.
 * <p>
 * SRP : cette classe a une <em>seule raison de changer</em> :
 * l'évolution des règles de validation métier d'un véhicule.
 * Si demain on ajoute une règle sur la marque, on ne touche
 * qu'à ce fichier, pas à {@code VehiculeService}.
 */
@Component
public class VehiculeValidator {

    /**
     * Vérifie que le véhicule est valide avant persistance.
     *
     * @param vehicule le véhicule à valider
     * @throws IllegalArgumentException si une règle métier est violée
     */
    public void valider(Vehicule vehicule) {
        if (vehicule == null) {
            throw new IllegalArgumentException("Le véhicule ne peut pas être null");
        }
        if (vehicule.getTarifJournalier() == null
                || vehicule.getTarifJournalier().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le tarif journalier doit être strictement positif");
        }
        if (vehicule.getImmatriculation() == null || vehicule.getImmatriculation().isBlank()) {
            throw new IllegalArgumentException("L'immatriculation est obligatoire");
        }
        if (vehicule.getMarque() == null || vehicule.getMarque().isBlank()) {
            throw new IllegalArgumentException("La marque est obligatoire");
        }
        if (vehicule.getModele() == null || vehicule.getModele().isBlank()) {
            throw new IllegalArgumentException("Le modèle est obligatoire");
        }
        if (vehicule.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        if (vehicule.getStatut() == null) {
            throw new IllegalArgumentException("Le statut est obligatoire");
        }
    }
}
