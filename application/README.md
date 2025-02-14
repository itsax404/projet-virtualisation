# Partie Application

## Partie Frontend
__*Réalisé par Joachim DRUHET*__

Cette partie permet de réaliser les calculs.
On a un historique des valeurs précédentes du calcul actuel.  
Aussi, on a le support du clavier (chiffres, `Entrée` pour calculer, `Echap` pour supprimer entièrement le calcul, `Suppr` pour supprimer la valeur actuelle).

**Ports utilisés :**
- 80

## Partie Backend 
__*Réalisé par Joachim DRUHET*__

Cette partie, réalisé en Python avec Flask, permet de faire le lien entre le Frontend et le Backend.  
Il a deux routes :
- **POST** `/api/calculate` : qui attend un body `{"operation": "x"}`.
- **GET** `/api/result/<operation>`.

**Ports utilisés :**
- 5000

Lors de la requête **POST**, le backend génère un UUID comme identifiant pour l'opération et ajoute le calcul au RabbitMQ.  
Lors de la requête **GET**, le backend demande au Redis la valeur associé à la clé `operation_id`.

## Partie Consumer 
__*Réalisé par Quentin DAVIN*__

Cette partie, réalisée en Java, permet de réaliser les calculs.  
Il _consume_ (c'est dans son nom) les messages dans la _queue_ `calcul` du RabbitMQ.
Il réalise le calcul et envoie le résultat dans la base de données Redis, avec comme clé le `operation_id` et comme valeur, le résultat.  
Si on divise par 0, on a comme valeur : `Erreur : Division par zéro`.

**Ports utilisés :**
- Aucun

## Partie RabbitMQ
__*Réalisé par Quentin DAVIN*__

Cette partie est juste pour avoir le module Management de RabbitMQ pour pouvoir déboguer cette partie.  
Mais, pour la partie **Kubernetes**, on utilise juste l'image sans Management de RabbitMQ

**Ports utilisés :**
- 5672

## Partie Redis

On a juste utilisé l'image de base de Redis.

**Ports utilisés :**
- 6379