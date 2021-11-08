# Journal

***
# Iteration 2

## Refactor

Le refactoring du code a pris plus de temps que prévu car dans l'itération 1 nous n'avions pas pensé l'architecture correctement. Notemment nous avons du intégrer maven au projet déja existant pour pouvoir correctement gérer les nouvelles dépendences liées aux fonctionnalités que nous avons implémenté. Maven nous permet de gérer très aisément les nouvelles APIs que nous ajoutons/ajouterons.
Nous avons ajouté nos propres exceptions qui sont maintenant correctement traitées.
Conformément aux retours de notre manager, nous avons séparé certaines classes qui devenaient trop importantes. (Diviser pour mieux régner)

## Importation-Exportation sur le cloud

Pour les importations et exportations sur le cloud nous avons choisi d'utiliser les services google drive ainsi que dropbox. Notre système de sauvegarde sur le cloud utilise un compte unique. Cela est plus pratique car il nous permet de facilement classer et récupérer les projets sauvegardés sur le cloud. Nous n'avons pas encore eu le temps d'implémenter l'exportation multiple de fichiers sur le cloud.

## Collaboration

Aucun problème n'a été rencontré lors de la conception de cette histoire si ce n'est un souci d'architecture qui a dû être corrigé au cours de la conception.

*** 
# Iteration 3 
## Test

La couverture des testes s'est grandement améliorée mais n'est toujours pas idéale. Nous nous sommes surtout focalisé sur les tests manquants des itérations précédentes par rapport aux DAO ainsi que les tests de DAO du système de geet. Concernant les tests de modèles, puisque nos modèles possèdent principalement des getters, des setters et des appels aus DAO (qui sont déjà testés), nous ne nous sommes pas focalisés sur leurs testing. Enfin, le testing de nos controllers s'est avéré un peu compliqué et va être reporté à l'itération 4. 

## Export multiple

Il est maintenant possible d'exporter plusieurs projets à la fois.

## Gestion des doublons

A présent, comme désiré par le client, il n'est plus possible de d'avoir 2 projets identiques, si l'on veut creer ou importer un projet ayant le même nom et la même date de fin on demande à l'utilisateur si il veut override ce projet ou bien annuler son action.

## Versioning

Le Versioning s'est avéré un peu plus difficile que prévu, étant donné qu'il y a dû avoir quelques changements de la structure de la base de données afin d'implémenter les versions et les branches.
L'interface du menu principal a aussi dû être modifiée afin d'être plus intuitive par rapport à l'utilisation du versioning et la structure des projets, ce qui a coûté plusieurs points de refactor.

## Stat
Nous avons privilégié la qualité du code et la résolution des issues que le client nous à donné, de ce fait nous avons dû reporter l'histoire 8 (statistiques) par manque de temps.

## Issues

Plusieurs issues ont dû être résolues pendant la troisième itération, et ont notamment empêché l'implémentation des tableaux de statistiques. Nous avons règler les derniers problèmes de division controller/viewcontroller, nos modèles ont été modifié pour une implémentation plus orienté objet, beaucoup plus de commentaires ont été ajoutés et la couverture des tests est plus étendue.

*** 
# Iteration 4

## Prioritisation des tâches

Nous avons décidé de laisser la prioritisation des tâches dans la vue des tâches. Chaque tâche possède maintenant une date de début et de fin et ces dates peuvent simplement être classée intuitivement dans la TableView par la propriété des colonnes des TableView permettant un classement automatique de nos dates. 

## Finalisation de Geet

Quelques bugs sur l'implémentation des branches ont été trouvés et on demandé du temps supplémentaire à leur résolution. La fonctionnalité de merge qui n'avait pas pu être finie pour l'itération 3 a été implémentée.

## Statistiques

Nous avons simplement utilisé les outils intégrés de SceneBuilder pour l'affichage des statistiques. Toutes les statistiques étant déjà extractables de notre base de données.Les statistiques peuvent aussi être exporté sous un format CSV comme demandé, et parce qu'on est d'une générositée sans nom, l'export en Json est également possible.

## Rappel interne et Externe
Les rappels internes à l'app n'ont demandé qu'un simple ajout d'une colonne dans la base de données. Ils sont définis à la création d'un projet ou d'une tâche. Une fois qu'un rappel apparait, la possibilité est laissée à l'utilisateur de renvoyer un rappel le lendemain.
Pour les rappels externes à l'app, nous avons décidé d'utiliser Google Calendar. Comme pour google drive, il est nécessaire que l'utilisateur suive les instructions qui s'ouvrent dans son navigateur pour créer le token qui liera l'application à l'API Google.

## Section d'aide 
Il a été décider de simplement associer des tooltips à chaque bouton de notre application et de faire un petit manuel contenant des gifs des actions principales de notre application. Ce manuel est consultable depuis la fenêtre principale.

## Mot de passe sur un projet
Tous les mots de passes sont mainantant cryptés dans l'application, ce qui n'était pas encore le cas (les mots de passes des utilisateurs étaient visibles en consultant la base de données par exemple). Il est maintenant surtout possible d'associer un mot de passe à un projet, ce qui n'a demandé qu'une légère modification de la table de projets dans notre base de données. Si un projet contenant un mot de passe est exporté, il ne pourra être importé que si le mot de passe est entré à l'importation. 
Le cryptage consiste en un simple hash à travers l'encrypteur BCrypt.

## Issues et refactoring
Cette itération a demandé très peu de refactoring, les problèmes d'architecture du projet étant plus locaux que sur les itérations précédentes. Les issues ont toute été résolue. On a fait des tests. Vraiment cette fois. 

## Team building 
Incroyable mais vrai, la team s'est finalement rencontrée. On a trouvé ça très XP. Les bières étaient bien fraîches.



