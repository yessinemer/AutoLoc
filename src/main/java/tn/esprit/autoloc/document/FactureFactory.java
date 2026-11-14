package tn.esprit.autoloc.document;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;

/**
 * Patron de conception GoF : <b>Factory Method</b> — Partie D.
 * <p>
 * <strong>Problème résolu</strong> : le code appelant n'a pas besoin de
 * connaître les classes concrètes ({@link GenerateurFacturePdf},
 * {@link GenerateurFactureExcel}). La factory centralise la logique
 * de création et garantit que l'on retourne toujours un
 * {@link GenerateurDocument} valide.
 * <p>
 * <strong>Avantages</strong> :
 * <ul>
 *   <li>Ajout d'un nouveau format (CSV, XML…) sans modifier les appelants.</li>
 *   <li>Un seul endroit à maintenir si la création devient complexe.</li>
 *   <li>Testabilité : on peut mocker la factory en test.</li>
 * </ul>
 */
@Component
public class FactureFactory {

    private final GenerateurFacturePdf generateurPdf;
    private final GenerateurFactureExcel generateurExcel;

    public FactureFactory(GenerateurFacturePdf generateurPdf,
                          GenerateurFactureExcel generateurExcel) {
        this.generateurPdf = generateurPdf;
        this.generateurExcel = generateurExcel;
    }

    /**
     * Retourne le générateur approprié selon le format demandé.
     *
     * @param format {@code "PDF"} ou {@code "EXCEL"} (insensible à la casse)
     * @return le générateur de document correspondant
     * @throws IllegalArgumentException si le format est inconnu
     */
    public GenerateurDocument creer(String format) {
        return switch (format.toUpperCase()) {
            case "PDF"   -> generateurPdf;
            case "EXCEL" -> generateurExcel;
            default      -> throw new IllegalArgumentException(
                    "Format de document inconnu : '" + format + "'. Formats disponibles : PDF, EXCEL");
        };
    }
}
