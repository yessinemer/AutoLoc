package tn.esprit.autoloc.tarification;

/**
 * Patron de conception GoF : <b>Strategy</b>.
 * <p>
 * Définit le contrat commun à toutes les politiques tarifaires.
 * Chaque implémentation encapsule <em>une seule</em> formule de calcul,
 * ce qui respecte à la fois SRP et OCP : ajouter une nouvelle politique
 * revient à créer une nouvelle classe sans toucher aux existantes.
 *
 * @see TarificationStandard
 * @see TarificationWeekend
 * @see TarificationLongueDuree
 * @see TarificationEntreprise
 */
public interface StrategieTarification {

    /**
     * Calcule le montant total de la location.
     *
     * @param tarifJournalier tarif de base du véhicule (en dinars)
     * @param jours           nombre de jours de location (doit être &gt; 0)
     * @return montant total calculé selon la politique tarifaire
     */
    double calculer(double tarifJournalier, int jours);
}
