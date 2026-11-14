package tn.esprit.autoloc.service;

/**
 * Contrat de journalisation (SRP + DIP).
 * <p>
 * SRP  : la journalisation est isolée dans sa propre abstraction,
 *         elle évolue indépendamment des règles métier.
 * DIP  : VehiculeService dépend de cette interface, pas d'une
 *         implémentation concrète (System.out, Slf4j, etc.).
 */
public interface JournalisationService {

    void info(String message);

    void warn(String message);

    void erreur(String message);
}
