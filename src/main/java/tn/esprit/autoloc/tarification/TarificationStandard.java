package tn.esprit.autoloc.tarification;

import org.springframework.stereotype.Component;

/**
 * Stratégie tarifaire standard : tarif journalier × nombre de jours.
 * <p>
 * OCP : cette classe est <em>fermée à la modification</em> ; si une autre
 * politique est requise, on crée une nouvelle classe sans toucher celle-ci.
 */
@Component("STANDARD")
public class TarificationStandard implements StrategieTarification {

    @Override
    public double calculer(double tarifJournalier, int jours) {
        return tarifJournalier * jours;
    }
}
