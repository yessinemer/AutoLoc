package tn.esprit.autoloc.document;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;

/**
 * Génère une facture au format PDF.
 * <p>
 * Dans un projet réel, cette classe utiliserait une bibliothèque comme
 * iText ou Apache PDFBox. Ici elle simule la génération pour l'atelier.
 */
@Component
public class GenerateurFacturePdf implements GenerateurDocument {

    @Override
    public byte[] generer(Contrat contrat) {
        // Simulation : dans un vrai projet, utiliser iText / Apache PDFBox
        String contenu = String.format(
                "FACTURE PDF\n===========\nContrat n° %d\nMontant : %.2f DT",
                contrat.getIdContrat(),
                contrat.getMontantTotal() != null ? contrat.getMontantTotal().doubleValue() : 0.0
        );
        return contenu.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public String getNomFichier(Contrat contrat) {
        return "facture-" + contrat.getIdContrat() + ".pdf";
    }
}
