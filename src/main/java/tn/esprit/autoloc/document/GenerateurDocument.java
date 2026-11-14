package tn.esprit.autoloc.document;

import tn.esprit.autoloc.domain.Contrat;

/**
 * Contrat commun à tous les générateurs de documents (Partie D — Factory).
 * <p>
 * La {@link FactureFactory} renvoie une implémentation de cette interface
 * sans que le code appelant n'ait besoin de connaître le type concret
 * (PDF ou Excel). C'est l'intérêt du patron <b>Factory Method</b> GoF.
 */
public interface GenerateurDocument {

    /**
     * Génère un document à partir du contrat fourni.
     *
     * @param contrat le contrat de location source
     * @return représentation binaire du document généré
     */
    byte[] generer(Contrat contrat);

    /**
     * Retourne le type MIME du document produit
     * (utile pour les en-têtes HTTP Content-Type).
     */
    String getContentType();

    /**
     * Retourne le nom de fichier suggéré pour le téléchargement.
     */
    String getNomFichier(Contrat contrat);
}
