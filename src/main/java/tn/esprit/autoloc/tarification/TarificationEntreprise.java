package tn.esprit.autoloc.tarification;

import org.springframework.stereotype.Component;

/**
 * Stratégie tarifaire entreprise : réduction de 15 %.
 * <p>
 * Formule : tarif journalier × jours × 0,85
 * <p>
 * OCP démontré : cette stratégie a été ajoutée (Étape 4 de l'atelier)
 * <strong>sans modifier</strong> {@link TarificationService} ni aucune
 * stratégie existante. Il suffit d'implémenter l'interface et d'annoter
 * la classe ; Spring l'enregistre automatiquement dans la Map.
 */
@Component("ENTREPRISE")
public class TarificationEntreprise implements StrategieTarification {

    private static final double REDUCTION = 0.85;

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours * REDUCTION;
    }
}
