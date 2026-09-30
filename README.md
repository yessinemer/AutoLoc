# AutoLoc

Plateforme de gestion de location de véhicules multi-agences.

Réalisé par : **Mohamed Yassine Merhbene**

## Objectifs du projet
- Permettre aux clients de consulter les véhicules disponibles et de réserver une location.
- Gérer les véhicules, les réservations et les contrats de location de plusieurs agences.
- Donner aux responsables d'agence une vue sur l'activité de leur agence.
- Centraliser l'administration de la plateforme (agences, utilisateurs).

## Acteurs
| Acteur | Rôle |
|---|---|
| Client | Consulte les véhicules, réserve, suit ses locations |
| Agent d'agence | Gère les réservations, les retraits et retours de véhicules |
| Responsable d'agence | Gère la flotte et les agents de son agence, suit l'activité |
| Administrateur | Gère les agences, les utilisateurs et la configuration globale |

## Cas d'utilisation (version initiale)
- Client : rechercher un véhicule, effectuer une réservation, annuler une réservation, consulter l'historique
- Agent d'agence : valider une réservation, enregistrer le départ / le retour d'un véhicule
- Responsable d'agence : ajouter / modifier un véhicule, gérer les agents, consulter les statistiques
- Administrateur : créer / gérer les agences, gérer les comptes utilisateurs

## Stack technique
Java 17, Spring Boot, Maven, Spring Data JPA, MySQL, Lombok, Git, Postman

## Environnement
Voir le dossier `docs/` pour la capture de l'environnement fonctionnel.