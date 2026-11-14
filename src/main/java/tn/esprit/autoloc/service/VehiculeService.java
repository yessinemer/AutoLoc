package tn.esprit.autoloc.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EquipementRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.tarification.TarificationService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Service métier pour la gestion des véhicules — Partie B, SRP + DIP.
 * <p>
 * <strong>SRP</strong> : ce service est responsable uniquement des
 * <em>opérations métier</em> sur les véhicules. La validation est
 * déléguée à {@link VehiculeValidator} et la journalisation à
 * {@link JournalisationService}. Chaque collaborateur a une seule
 * raison de changer, indépendamment des autres.
 * <p>
 * <strong>DIP</strong> : ce service dépend :
 * <ul>
 *   <li>de {@code VehiculeRepository} (interface JPA fournie par Spring Data)</li>
 *   <li>de {@code JournalisationService} (interface, pas {@code System.out})</li>
 *   <li>de {@code TarificationService} (délègue le calcul)</li>
 * </ul>
 * Spring injecte les implémentations concrètes via le constructeur (injection
 * de dépendances = mécanisme d'application du DIP).
 * <p>
 * <strong>ISP</strong> : implémente {@link VehiculeConsultationService} et
 * {@link VehiculeModificationService} pour que les consommateurs ne dépendent
 * que du contrat dont ils ont besoin.
 */
@Service
public class VehiculeService extends AbstractCrudService<Vehicule>
        implements VehiculeConsultationService, VehiculeModificationService {

    // ------------------------------------------------------------------ //
    //  DIP : dépendances sur des ABSTRACTIONS, jamais sur des classes concrètes
    // ------------------------------------------------------------------ //

    /** Contrat de persistance — Spring Data injecte l'implémentation proxy JPA. */
    private final VehiculeRepository vehiculeRepository;

    /** SRP + DIP : la journalisation est externalisée dans une abstraction. */
    private final JournalisationService journalisationService;

    /** SRP : la validation des règles métier est déléguée à un composant dédié. */
    private final VehiculeValidator vehiculeValidator;

    /** GoF Strategy + SRP : le calcul tarifaire est délégué à TarificationService. */
    private final TarificationService tarificationService;

    // ------------------------------------------------------------------ //
    //  Injection de dépendances par constructeur (bonne pratique Spring)
    // ------------------------------------------------------------------ //

    private final AgenceRepository agenceRepository;
    private final EquipementRepository equipementRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                           JournalisationService journalisationService,
                           VehiculeValidator vehiculeValidator,
                           TarificationService tarificationService,
                           AgenceRepository agenceRepository,
                           EquipementRepository equipementRepository) {
        super(vehiculeRepository, "Vehicule");
        this.vehiculeRepository      = vehiculeRepository;
        this.journalisationService   = journalisationService;
        this.vehiculeValidator       = vehiculeValidator;
        this.tarificationService     = tarificationService;
        this.agenceRepository        = agenceRepository;
        this.equipementRepository    = equipementRepository;
    }

    // ------------------------------------------------------------------ //
    //  Méthode ajouter — SRP démontré : chaque étape est déléguée
    // ------------------------------------------------------------------ //

    /**
     * Valide puis persiste un véhicule.
     * <p>
     * Avant refactoring : validation + journalisation + persistance étaient
     * mélangées ici. Après SRP : chaque responsabilité est dans sa propre classe.
     */
    @Override
    public Vehicule ajouter(Vehicule vehicule) {
        // 1. Validation déléguée à VehiculeValidator (SRP)
        vehiculeValidator.valider(vehicule);

        // 2. Journalisation déléguée à JournalisationService (SRP + DIP)
        journalisationService.info("Ajout du véhicule : " + vehicule.getImmatriculation());

        // 3. Persistance via le repository (DIP : on dépend du contrat JpaRepository)
        return save(vehicule);
    }

    // ------------------------------------------------------------------ //
    //  Méthode modifier — ISP VehiculeModificationService
    // ------------------------------------------------------------------ //

    @Override
    public Vehicule modifier(Long id, Vehicule vehiculeModifie) {
        Vehicule existant = findById(id);
        existant.setImmatriculation(vehiculeModifie.getImmatriculation());
        existant.setMarque(vehiculeModifie.getMarque());
        existant.setModele(vehiculeModifie.getModele());
        existant.setCategorie(vehiculeModifie.getCategorie());
        existant.setTarifJournalier(vehiculeModifie.getTarifJournalier());
        existant.setStatut(vehiculeModifie.getStatut());
        journalisationService.info("Modification du véhicule id=" + id);
        return save(existant);
    }

    // ------------------------------------------------------------------ //
    //  Méthode supprimer — ISP VehiculeModificationService
    // ------------------------------------------------------------------ //

    @Override
    public void supprimer(Long id) {
        journalisationService.info("Suppression du véhicule id=" + id);
        deleteById(id);
    }

    // ------------------------------------------------------------------ //
    //  Méthodes ISP VehiculeConsultationService
    // ------------------------------------------------------------------ //

    @Override
    public Vehicule consulter(Long id) {
        return findById(id);
    }

    @Override
    public List<Vehicule> consulterTous() {
        return findAll();
    }

    // ------------------------------------------------------------------ //
    //  Calcul tarifaire — délégué au patron Strategy (GoF)
    // ------------------------------------------------------------------ //

    /**
     * Calcule le tarif d'une location en déléguant à {@link TarificationService}.
     *
     * @param vehicule      le véhicule concerné
     * @param jours         nombre de jours
     * @param typeStrategie ex. {@code "STANDARD"}, {@code "WEEKEND"}, {@code "LONGUE_DUREE"}, {@code "ENTREPRISE"}
     * @return montant total calculé
     */
    public double calculerTarif(Vehicule vehicule, int jours, String typeStrategie) {
        return tarificationService.calculer(typeStrategie, vehicule, jours);
    }

    // ------------------------------------------------------------------ //
    //  Requêtes spécifiques au domaine véhicule
    // ------------------------------------------------------------------ //

    public Vehicule findByImmatriculation(String immatriculation) {
        return vehiculeRepository.findByImmatriculation(immatriculation).orElse(null);
    }

    public List<Vehicule> findByStatut(StatutVehicule statut) {
        return vehiculeRepository.findByStatut(statut);
    }

    public List<Vehicule> findByCategorie(CategorieVehicule categorie) {
        return vehiculeRepository.findByCategorie(categorie);
    }

    public List<Vehicule> findByAgence(Long agenceId) {
        return vehiculeRepository.findByAgence_IdAgence(agenceId);
    }

    public List<Vehicule> findDisponibles(LocalDate debut, LocalDate fin,
                                          Collection<StatutReservation> statuts) {
        return vehiculeRepository.findDisponibles(debut, fin, statuts);
    }

    // ------------------------------------------------------------------ //
    //  Scénario 2 — @ManyToOne : rattachement d'un véhicule à une agence
    // ------------------------------------------------------------------ //

    /**
     * Rattache un véhicule à l'agence indiquée.
     * <p>
     * Relation {@code @ManyToOne} côté {@code Vehicule} : c'est le propriétaire
     * de la FK {@code agence_id}. Un seul UPDATE sur la table {@code vehicule}.
     *
     * @param idVehicule identifiant du véhicule
     * @param idAgence   identifiant de l'agence cible
     * @return le véhicule mis à jour
     */
    public Vehicule affecterVehicule(Long idVehicule, Long idAgence) {
        Vehicule vehicule = findById(idVehicule);
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agence introuvable avec l'id " + idAgence));
        vehicule.setAgence(agence);
        journalisationService.info(
                "Véhicule " + vehicule.getImmatriculation() + " rattaché à l'agence " + agence.getNom());
        return save(vehicule);
    }

    // ------------------------------------------------------------------ //
    //  Scénario 3 — @ManyToMany : équipements installés / retirés
    // ------------------------------------------------------------------ //

    /**
     * Installe un équipement sur un véhicule.
     * <p>
     * Relation {@code @ManyToMany} : {@code Vehicule} est le propriétaire
     * (table de jointure {@code vehicule_equipement}). On gère les <em>deux côtés</em>
     * en mémoire pour maintenir la cohérence du contexte de persistance JPA.
     *
     * @param idVehicule   identifiant du véhicule
     * @param idEquipement identifiant de l'équipement à installer
     * @return le véhicule mis à jour
     * @throws IllegalStateException si l'équipement est déjà installé
     */
    public Vehicule affecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = findById(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Equipement introuvable avec l'id " + idEquipement));
        if (vehicule.getEquipements().contains(equipement)) {
            throw new IllegalStateException(
                    "L'équipement '" + equipement.getLibelle()
                    + "' est déjà installé sur le véhicule " + vehicule.getImmatriculation());
        }
        // Propriétaire de la relation : on met à jour le Set<Equipement> du véhicule
        vehicule.getEquipements().add(equipement);
        // Côté inverse : maintenu en mémoire pour la cohérence
        equipement.getVehicules().add(vehicule);
        journalisationService.info(
                "Équipement '" + equipement.getLibelle()
                + "' installé sur " + vehicule.getImmatriculation());
        return save(vehicule);
    }

    /**
     * Retire un équipement d'un véhicule.
     * <p>
     * Supprime la ligne dans la table de jointure {@code vehicule_equipement}.
     * Ni le véhicule ni l'équipement ne sont supprimés de leur table respective.
     *
     * @param idVehicule   identifiant du véhicule
     * @param idEquipement identifiant de l'équipement à retirer
     * @return le véhicule mis à jour
     * @throws IllegalStateException si l'équipement n'est pas installé sur ce véhicule
     */
    public Vehicule desaffecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = findById(idVehicule);
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Equipement introuvable avec l'id " + idEquipement));
        if (!vehicule.getEquipements().contains(equipement)) {
            throw new IllegalStateException(
                    "L'équipement '" + equipement.getLibelle()
                    + "' n'est pas installé sur le véhicule " + vehicule.getImmatriculation());
        }
        vehicule.getEquipements().remove(equipement);
        equipement.getVehicules().remove(vehicule);
        journalisationService.info(
                "Équipement '" + equipement.getLibelle()
                + "' retiré du véhicule " + vehicule.getImmatriculation());
        return save(vehicule);
    }
}
