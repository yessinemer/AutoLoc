package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;

/**
 * Interface ISP — modification des véhicules — Partie D, Situation 2.
 * <p>
 * ISP : un composant qui a besoin d'<em>écrire</em> (ajouter, modifier,
 * supprimer) dépend uniquement de ce contrat. Il ne dépend pas des
 * opérations de lecture, d'export ou de notification.
 */
public interface VehiculeModificationService {

    Vehicule ajouter(Vehicule vehicule);

    Vehicule modifier(Long id, Vehicule vehicule);

    void supprimer(Long id);
}
