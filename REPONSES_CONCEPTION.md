# Réponses de conception — Atelier SOLID & GoF — AutoLoc

---

## Partie A — Identifier les problèmes de conception

### 1. Responsabilités de `VehiculeService` (avant refactoring)

| Responsabilité | Code concerné |
|----------------|--------------|
| **Validation métier** | `if (vehicule.getTarifJournalier() <= 0)` |
| **Journalisation** | `System.out.println("Ajout du véhicule")` |
| **Gestion de la persistance** | `repository.save(vehicule)` |
| **Calcul tarifaire** | `vehicule.getTarifJournalier() * nombreJours` |

La classe avait donc **4 raisons de changer** au lieu d'une seule.

### 2. Pourquoi ces trois aspects évolueraient-ils indépendamment ?

- **Validation** : si une nouvelle règle métier est ajoutée (ex. immatriculation unique,
  catégorie obligatoire), seule la validation change.
- **Journalisation** : si on passe de `System.out` à SLF4J puis à une
  solution de log centralisée (ELK, Datadog), seule la journalisation change.
- **Gestion des véhicules** : si on ajoute une recherche par agence ou
  par disponibilité, seule la logique métier change.

Mélanger ces trois aspects dans une même classe crée un couplage inutile :
modifier l'une des parties risque d'introduire des régressions dans les autres.

### 3. Principe SOLID concerné

**SRP — Single Responsibility Principle** :
> *"A class should have only one reason to change."* (Robert C. Martin)

Une classe doit encapsuler une seule préoccupation. Si elle en contient plusieurs,
chaque changement dans l'une d'elles impose une recompilation et un risque de
régression sur les autres.

### 4. Répartition proposée

```
VehiculeService      → orchestration des opérations métier sur les véhicules
VehiculeValidator    → validation des règles métier (tarif, immatriculation, etc.)
JournalisationService → journalisation (interface) / Slf4jJournalisationService (impl)
TarificationService  → calcul du prix selon la stratégie choisie (GoF Strategy)
```

---

## Partie B — Appliquer SRP et DIP

### B.1 — Refactoring SRP : méthode `ajouter()`

**Avant** : tout dans une méthode

```java
public Vehicule ajouter(Vehicule vehicule) {
    if (vehicule.getTarifJournalier() <= 0) {          // validation
        throw new IllegalArgumentException("...");
    }
    System.out.println("Ajout du véhicule");           // journalisation
    return repository.save(vehicule);                  // persistance
}
```

**Après** : chaque responsabilité déléguée

```java
public Vehicule ajouter(Vehicule vehicule) {
    vehiculeValidator.valider(vehicule);               // → VehiculeValidator
    journalisationService.info("Ajout du véhicule : "
        + vehicule.getImmatriculation());              // → JournalisationService
    return save(vehicule);                             // → AbstractCrudService / JPA
}
```

Chaque collaborateur a **une seule raison de changer**.

### B.2 — Principe d'inversion des dépendances (DIP)

#### 1. Rôle de `VehiculeRepository`

`VehiculeRepository` est une **interface** Spring Data JPA. Elle définit un
**contrat de persistance** (findById, save, findAll, findByStatut...) sans
exposer les détails de la base de données.

#### 2. Pourquoi dépendre d'un contrat ?

Si `VehiculeService` créait lui-même son mécanisme de persistance :

```java
// Sans DIP : couplage fort
EntityManager em = Persistence.createEntityManagerFactory("autoloc").createEntityManager();
```

- Impossible de tester sans base de données réelle.
- Impossible de changer de stratégie de persistance (JPA vers MongoDB) sans modifier le service.
- Violation du DIP : les modules de haut niveau (service) ne doivent pas
  dépendre des modules de bas niveau (JDBC, Hibernate) mais de leurs **abstractions**.

#### 3. Comment Spring injecte le repository

```java
// Spring génère dynamiquement une implémentation proxy de VehiculeRepository
// via SimpleJpaRepository<Vehicule, Long> au démarrage.
// Le constructeur de VehiculeService reçoit cette implémentation :

public VehiculeService(VehiculeRepository vehiculeRepository, ...) {
    super(vehiculeRepository, "Vehicule");
    this.vehiculeRepository = vehiculeRepository;
    // Spring a résolu VehiculeRepository -> SimpleJpaRepository (Hibernate + H2/MySQL)
}
```

L'annotation `@Repository` sur l'interface suffit ; Spring crée le bean
et l'injecte partout où le type `VehiculeRepository` est attendu.

#### 4. Lien entre DIP et l'injection de dépendances (DI)

| DIP (principe) | DI (mécanisme) |
|----------------|---------------|
| "Dépendre des abstractions" | Spring fournit les implémentations concrètes |
| Défini dans le code (interfaces) | Appliqué à l'exécution (IoC Container) |
| Principe de conception | Patron architectural (IoC) |

> Le DIP dit **quoi** : dépendre des abstractions.
> L'injection de dépendances dit **comment** : confier la création et la résolution
> des dépendances à un conteneur externe (Spring).

Dans notre projet, `VehiculeService` dépend de :
- `VehiculeRepository` (interface JPA)
- `JournalisationService` (interface maison)
- `VehiculeValidator` (composant Spring)
- `TarificationService` (service Spring)

Aucune de ces dépendances n'est instanciée avec `new` → **DIP respecté**.

---

## Partie C — GoF Strategy et OCP

### Q1. Quel problème le patron Strategy résout-il ?

Sans Strategy, le calcul tarifaire ressemblerait à :

```java
// Avant Strategy : if/else enchainés — viole OCP
public double calculer(String type, double tarif, int jours) {
    if ("STANDARD".equals(type))      return tarif * jours;
    if ("WEEKEND".equals(type))       return tarif * jours * 0.90;
    if ("LONGUE_DUREE".equals(type))  return tarif * jours * 0.80;
    // Chaque nouvelle politique force une modification ici -> OCP violé
}
```

**Strategy résout** : chaque politique est **encapsulée** dans sa propre classe.
Le service ne connaît que l'interface `StrategieTarification`.
Ajouter une politique = créer une nouvelle classe, sans toucher l'existant.

### Q2. Pourquoi isoler chaque formule dans une classe différente ?

- **Testabilité** : on teste `TarificationWeekend` seule, sans dépendance.
- **Lisibilité** : chaque classe documente clairement sa formule.
- **SRP** : une classe = une formule = une raison de changer.
- **OCP** : l'ajout d'une politique ne modifie pas les classes existantes.

### Q3. Comment cette conception applique-t-elle OCP ?

> *"Software entities should be open for extension, but closed for modification."*

| | Avant Strategy | Avec Strategy |
|---|---|---|
| Nouvelle politique | Modifier `calculer()` | Créer une nouvelle classe |
| Risque de régression | Élevé | Nul |
| Tests à re-exécuter | Tous | Seulement la nouvelle classe |

**Démonstration** : `TarificationEntreprise` a été ajoutée (Étape 4)
**sans modifier** `TarificationService`. Spring l'a automatiquement enregistrée
dans la `Map<String, StrategieTarification>` grâce à `@Component("ENTREPRISE")`.

### Q4. Que faire si le type demandé n'existe pas ?

```java
// Dans TarificationService.calculer() :
StrategieTarification strategie = strategies.get(typeStrategie.toUpperCase());
if (strategie == null) {
    throw new IllegalArgumentException(
        "Type de tarification inconnu : '" + typeStrategie + "'. "
        + "Types disponibles : " + strategies.keySet()
    );
}
```

Le service lève une `IllegalArgumentException` avec un message explicatif
listant les types disponibles. Le contrôleur REST laisse remonter l'exception
qui est interceptée par `ApiExceptionHandler` → réponse HTTP 400.

---

## Partie D — Factory, ISP, LSP

### Situation 1 — Factory

#### Problème résolu par `FactureFactory`

Sans factory, le code appelant doit choisir entre `GenerateurFacturePdf`
et `GenerateurFactureExcel` directement :

```java
// Couplage fort avec les classes concrètes
if ("PDF".equals(format)) {
    new GenerateurFacturePdf().generer(contrat);
} else {
    new GenerateurFactureExcel().generer(contrat);
}
```

**La factory centralise** cette logique de création en un seul endroit.

#### Méthode exposée

```java
public GenerateurDocument creer(String format) {
    return switch (format.toUpperCase()) {
        case "PDF"   -> generateurPdf;
        case "EXCEL" -> generateurExcel;
        default      -> throw new IllegalArgumentException("Format inconnu : " + format);
    };
}
```

#### Avantages

| Avantage | Explication |
|----------|-------------|
| **Découplage** | Le code appelant ne connaît que `GenerateurDocument` |
| **Extensibilité** | Ajouter CSV = ajouter un case + une classe |
| **Maintenabilité** | Un seul endroit à modifier si la logique de création change |
| **Testabilité** | On peut mocker la factory en test |

---

### Situation 2 — ISP (Interface Segregation Principle)

#### Interface originale (violation ISP)

```java
// Interface trop large : un lecteur dépend de supprimer() et envoyerNotification()
public interface GestionVehicule {
    Vehicule ajouter(Vehicule vehicule);
    Vehicule consulter(Long id);
    Vehicule modifier(Long id, Vehicule vehicule);
    void supprimer(Long id);
    void exporterRapport();         // non lié à la gestion CRUD
    void envoyerNotification();     // non lié à la gestion CRUD
}
```

Un composant de **consultation** dépend de `supprimer()` et `envoyerNotification()`
qu'il n'utilisera jamais → si ces méthodes changent, il est quand même recompilé.

#### Solution ISP — interfaces séparées

```java
// VehiculeConsultationService.java
public interface VehiculeConsultationService {
    Vehicule consulter(Long id);
    List<Vehicule> consulterTous();
}

// VehiculeModificationService.java
public interface VehiculeModificationService {
    Vehicule ajouter(Vehicule vehicule);
    Vehicule modifier(Long id, Vehicule vehicule);
    void supprimer(Long id);
}

// D'autres interfaces pourraient gérer export et notification séparément
```

`VehiculeService` implémente les deux contrats. Un composant de rapport
dépend uniquement de `VehiculeConsultationService`.

---

### Situation 3 — LSP (Liskov Substitution Principle)

#### Violation du LSP

```java
// VehiculeElectrique étend Vehicule mais refuse une opération héritée
public class VehiculeElectrique extends Vehicule {
    @Override
    public void faireCarburant() {
        throw new UnsupportedOperationException("Les véhicules électriques ne font pas le plein");
    }
}
```

**Problème** : tout code qui utilise un `Vehicule` et appelle `faireCarburant()`
plantera en runtime si on lui passe un `VehiculeElectrique`. Le sous-type
n'est **pas substituable** à son parent.

> LSP : *"Si S est un sous-type de T, les objets de type T dans un programme
> peuvent être remplacés par des objets de type S sans altérer le comportement."*

#### Solution

**Option 1** — Ne pas hériter si le comportement est incompatible.
Utiliser la **composition** plutôt que l'héritage.

**Option 2** — Repenser la hiérarchie avec une abstraction qui ne force
pas `faireCarburant()` pour tous :

```java
// Abstraction correcte
public abstract class Vehicule {
    // Méthodes communes à tous les véhicules
}

public interface Thermique {
    void faireCarburant();
}

public class VehiculeEssence extends Vehicule implements Thermique {
    @Override public void faireCarburant() { /* logique essence */ }
}

public class VehiculeElectrique extends Vehicule {
    public void recharger() { /* logique recharge */ }
}
```

Maintenant `VehiculeElectrique` ne prétend pas pouvoir `faireCarburant()`,
LSP est respecté et aucun `UnsupportedOperationException` ne surgit.

---

## Synthèse — Principes SOLID et Patrons GoF utilisés

### Principes SOLID

| Principe | Application dans AutoLoc |
|----------|--------------------------|
| **SRP** — Single Responsibility | `VehiculeValidator` (validation), `JournalisationService` (logs), `TarificationService` (calcul) — chacun une raison de changer |
| **OCP** — Open/Closed | `StrategieTarification` : nouvelles politiques ajoutées sans modifier `TarificationService` |
| **LSP** — Liskov Substitution | Toute sous-classe de `Vehicule` doit respecter les contrats de la classe mère (analyse Situation 3) |
| **ISP** — Interface Segregation | `VehiculeConsultationService` et `VehiculeModificationService` : interfaces spécialisées à grain fin |
| **DIP** — Dependency Inversion | `VehiculeService` dépend de `VehiculeRepository` (interface JPA), `JournalisationService` (interface), jamais de `System.out` ou `Hibernate` directement |

### Patrons GoF utilisés

| Patron | Catégorie | Application |
|--------|-----------|-------------|
| **Strategy** | Comportemental | `StrategieTarification` + 4 implémentations. `TarificationService` utilise la stratégie sans la connaître concrètement. Résout l'ajout de nouvelles formules sans modification. |
| **Factory Method** | Création | `FactureFactory.creer(String format)` retourne `GenerateurDocument` (PDF ou Excel) sans exposer les classes concrètes au code appelant. |

### Lien SOLID ↔ GoF

```
Strategy  →  OCP (nouvelles stratégies sans modifier le service)
             SRP (une classe = une formule)
             DIP (service dépend de l'interface StrategieTarification)

Factory   →  OCP (nouveau format sans modifier le code appelant)
             DIP (code appelant dépend de GenerateurDocument, pas de Pdf/Excel)
             ISP (GenerateurDocument n'expose que generer/contentType/nomFichier)
```

> Les patrons GoF sont souvent des **instanciations concrètes** des principes SOLID :
> appliquer Strategy, c'est appliquer simultanément SRP, OCP et DIP.
