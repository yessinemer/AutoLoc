package tn.esprit.autoloc.document;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;

/**
 * Génère une facture au format Excel.
 * <p>
 * Dans un projet réel, cette classe utiliserait Apache POI.
 * Ici elle simule la génération pour l'atelier.
 */
@Component
public class GenerateurFactureExcel implements GenerateurDocument {

    @Override
    public byte[] generer(Contrat contrat) {
        // Simulation : dans un vrai projet, utiliser Apache POI
        String contenu = String.format(
                "FACTURE EXCEL\n=============\nContrat n° %d\nMontant : %.2f DT",
                contrat.getIdContrat(),
                contrat.getMontantTotal() != null ? contrat.getMontantTotal().doubleValue() : 0.0
        );
        return contenu.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getNomFichier(Contrat contrat) {
        return "facture-" + contrat.getIdContrat() + ".xlsx";
    }
}
