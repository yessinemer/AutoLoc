package tn.esprit.autoloc.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.repository.AgenceRepository;
import tn.esprit.autoloc.repository.EmployeRepository;

import java.util.List;

@Service
public class EmployeService extends AbstractCrudService<Employe> {

    private final EmployeRepository employeRepository;
    private final AgenceRepository  agenceRepository;

    public EmployeService(EmployeRepository employeRepository,
                          AgenceRepository agenceRepository) {
        super(employeRepository, "Employe");
        this.employeRepository = employeRepository;
        this.agenceRepository  = agenceRepository;
    }

    // ------------------------------------------------------------------ //
    //  Scénario 1 — @ManyToOne : mutation RH
    //  Un employé ne peut appartenir qu'à une seule agence à la fois.
    //  Le transfert passe d'abord par une désaffectation, puis une
    //  réaffectation vers la nouvelle agence.
    // ------------------------------------------------------------------ //

    /**
     * Désaffecte un employé de son agence courante.
     * <p>
     * La relation {@code @ManyToOne} {@code Employe.agence} est mise à
     * {@code null}. L'agence elle-même n'est pas supprimée : seul le lien
     * FK {@code agence_id} de la table {@code employe} est effacé.
     *
     * @param idEmploye identifiant de l'employé à désaffecter
     * @return l'employé mis à jour (sans agence)
     * @throws IllegalStateException si l'employé n'est rattaché à aucune agence
     */
    public Employe desaffecterEmploye(Long idEmploye) {
        Employe employe = findById(idEmploye);
        if (employe.getAgence() == null) {
            throw new IllegalStateException(
                    "L'employé id=" + idEmploye + " n'est rattaché à aucune agence.");
        }
        employe.setAgence(null);
        return save(employe);
    }

    /**
     * Affecte un employé à une agence cible.
     * <p>
     * Côté base de données, seule la FK {@code agence_id} de la table
     * {@code employe} est mise à jour — la table {@code agence} n'est pas
     * touchée (relation {@code @ManyToOne} : le propriétaire est {@code Employe}).
     *
     * @param idEmploye identifiant de l'employé
     * @param idAgence  identifiant de l'agence cible
     * @return l'employé mis à jour avec sa nouvelle agence
     */
    public Employe affecterEmploye(Long idEmploye, Long idAgence) {
        Employe employe = findById(idEmploye);
        Agence agence = agenceRepository.findById(idAgence)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agence introuvable avec l'id " + idAgence));
        employe.setAgence(agence);
        return save(employe);
    }

    // ------------------------------------------------------------------ //
    //  Requêtes existantes
    // ------------------------------------------------------------------ //

    public List<Employe> findByAgence(Long agenceId) {
        return employeRepository.findByAgence_IdAgence(agenceId);
    }

    public List<Employe> findByRole(RoleEmploye role) {
        return employeRepository.findByRole(role);
    }

    public List<Employe> findByAgenceAndRole(Long agenceId, RoleEmploye role) {
        return employeRepository.findByAgence_IdAgenceAndRole(agenceId, role);
    }
}
