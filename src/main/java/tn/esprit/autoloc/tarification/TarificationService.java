package tn.esprit.autoloc.tarification;

import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Vehicule;

import java.util.Map;

/**
 * Service de calcul tarifaire — Partie C, patron <b>Strategy</b> GoF.
 * <p>
 * Spring injecte automatiquement un {@code Map<String, StrategieTarification>}
 * dont la <em>clé</em> correspond au qualifier de chaque bean
 * ({@code "STANDARD"}, {@code "WEEKEND"}, {@code "LONGUE_DUREE"}, {@code "ENTREPRISE"}).
 * <p>
 * <strong>OCP</strong> : pour ajouter une nouvelle politique tarifaire il suffit
 * de créer une classe annotée {@code @Component("NOM")} implémentant
 * {@link StrategieTarification} — ce service n'est <em>jamais modifié</em>.
 * <p>
 * <strong>DIP</strong> : ce service dépend de l'interface {@link StrategieTarification},
 * jamais des classes concrètes.
 */
@Service
public class TarificationService {

    private final Map<String, StrategieTarification> strategies;

    /**
     * Spring résout automatiquement cette Map :
     * chaque bean {@link StrategieTarification} est enregistré avec son qualifier comme clé.
     */
    public TarificationService(Map<String, StrategieTarification> strategies) {
        this.strategies = strategies;
    }

    /**
     * Calcule le montant total pour un véhicule selon la politique tarifaire demandée.
     *
     * @param typeStrategie identifiant de la stratégie
     *                      ({@code STANDARD}, {@code WEEKEND}, {@code LONGUE_DUREE}, {@code ENTREPRISE})
     * @param vehicule      le véhicule concerné
     * @param jours         nombre de jours de location
     * @return montant total calculé
     * @throws IllegalArgumentException si le type de stratégie est inconnu ou si jours &lt;= 0
     */
    public double calculer(String typeStrategie, Vehicule vehicule, int jours) {
        if (jours <= 0) {
            throw new IllegalArgumentException("Le nombre de jours doit être strictement positif");
        }
        StrategieTarification strategie = strategies.get(typeStrategie.toUpperCase());
        if (strategie == null) {
            throw new IllegalArgumentException(
                    "Type de tarification inconnu : '" + typeStrategie + "'. "
                    + "Types disponibles : " + strategies.keySet());
        }
        double tarifJournalier = vehicule.getTarifJournalier().doubleValue();
        return strategie.calculer(tarifJournalier, jours);
    }

    /**
     * Retourne la liste des types de tarification disponibles.
     */
    public java.util.Set<String> getTypesDisponibles() {
        return strategies.keySet();
    }
}
