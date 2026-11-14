package tn.esprit.autoloc.tarification;

import org.springframework.stereotype.Component;

/**
 * Stratégie tarifaire longue durée : réduction de 20 %.
 * <p>
 * Formule : tarif journalier × jours × 0,80
 */
@Component("LONGUE_DUREE")
public class TarificationLongueDuree implements StrategieTarification {

    private static final double REDUCTION = 0.80;

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours * REDUCTION;
    }
}
