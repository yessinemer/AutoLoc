package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

/**
 * Interface ISP — consultation des véhicules — Partie D, Situation 2.
 * <p>
 * ISP : un composant qui a uniquement besoin de <em>consulter</em> les
 * véhicules dépend de ce contrat minimal, pas de l'ensemble des opérations
 * CRUD. Si demain la méthode {@code exporterRapport} change, ce composant
 * n'est pas recompilé ni impacté.
 */
public interface VehiculeConsultationService {

    Vehicule consulter(Long id);

    List<Vehicule> consulterTous();
}
