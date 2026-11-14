package tn.esprit.autoloc.tarification;

import org.springframework.stereotype.Component;

/**
 * Stratégie tarifaire week-end : réduction de 10 %.
 * <p>
 * Formule : tarif journalier × jours × 0,90
 */
@Component("WEEKEND")
public class TarificationWeekend implements StrategieTarification {

    private static final double REDUCTION = 0.90;

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours * REDUCTION;
    }
}
