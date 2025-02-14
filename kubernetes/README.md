# Partie Kubernetes

Dans cette partie, nous avons séparés les fichiers par type :
- le dossier `./replicas` contient les fichiers pour lancer les cinq RéplicaSet
- le dossier `./ingress` contient le fichier pour appliquer les règles Ingress
- le dossier `./services` contient les fichiers pour les quatre Service.

## Les RéplicaSet

> [!WARNING]
> On a des gros soucis avec l'actualisation des images.  
> On a beau pousser des nouvelles versions, elles se mettent pas à jour, empêchant de corriger des bugs, ou de tester les nouvelles fonctionnalités qu'on ajoute.  
> Cependant, on a pu tester la connexion entre les micro-services et cela doit marcher.  
> (le frontend a une favicon dans la dernière version)  

On a cinq RéplicaSet :
- `frontend-replica`
- `backend-replica`
- `consumer-replica` 
- `rabbit-replica`
- `redis-replica`

On a mis le nombre de réplicas à 3 et on a limité les ressources de chaque Pod crée à 4 milliCPU (0,004% de CPU) et à 32 MiO.

## Les Services

On a crée des Services pour attribuer des adresses IP privés aux ReplicaSet.  
Ainsi, on a quatre services :
- `frontend-service`
- `backend-service`
- `redis-service`
- `rabbit-service`
_le consumer n'a pas besoin d'adresse IP privé car il n'est pas appelé dans des requêtes_

> [!INFO]
> Après des jours de souffrance, on a découvert qu'on peut utiliser le nom des services comme IP
> afin que les Pods récupèrent automatiquent l'IP (car les Service ont des IP dynamiques)

## Les règles Ingress

On a crée un fichier afin de faire les règles Ingress.
On a fait en sorte que l'URL http://calculatrice-davin-druhet.polytech-dijon.kiowy.net/ retourne dans le Prefix `/`.
Ensuite on a défini ces Prefix: 
- `/` : cela renvoit au Service `frontend-service`
- `/api` : cela renvoit au Service `backend-service`
