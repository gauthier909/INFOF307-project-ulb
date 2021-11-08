# Histoires
Informations récapitulatives concernant les différentes histoires.

#### Quelques précisions
Un point correspond à une heure de travail par binôme (approximatif).  Par itération il faut accomplir X points.

----------------------


## Pondération

| Priorité/3 | N° | Description | Difficulté/3 | Risque/3 | Heures/? | Points | Done |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ |
| 1 | [1](#Histoire-A) | Histoire A | 3 | 3 | 20 | 20 |X
| 2 | 2 | Histoire B | 2 | 2 | 30 | 30 |X
| 2 | 3 | Histoire C | 2 | 2 | 15 | 15 | 
| 2 | 4 | Histoire D | 2 | 2 | 20 | 20 |X
| 2 | 5 | Histoire E | 2 | 2 | 8 | 8 |To finish
| 2 | 6 | Histoire F | 3 | 3 | 32 | 32 |X
| 2 | 7 | Histoire G | 3 | 3 | 15 | 15 |X
| 2 | 8 | Histoire H | 2 | 2 | 18 | 18 | 
| 2 | 9 | Histoire I | 1 | 1 | 40 | 40 |X
| 3 | 10 | Histoire J | 2 | 2 | 20 | 20 | 
| 3 | 11 | Histoire K | 2 | 2 | 14 | 14 | 
| 3 | 12 | Histoire L | 1 | 1 | 34 | 34 | 
| 3 | 13 | Histoire M | 1 | 1 | 40 | 40 | 

----------------------


## Tableau Risque-priorité

| &#8595; Priorité / Risque &#8594; | 1 | 2 | 3 | 
| ------ | ------ | ------ | ------ |
| 1 | / | /  | 1 |
| 2 | 5 9 | 2 3 4 8 | 6 7  |
| 3 | 12 13 | 10 11  | / |


----------------------

## Description

### Histoire 1


**Découpement des tâches:**  

- Création d'un compte 10
	- Accepter les conditions de service.
	- Vérifier la validité des données saisies.
- Se connecter 4
- Modifier le profil 6
	- Vérifier la validité des données saisies.

:question: **Question:**       
- Format du stockage des données ? Sécurisé ?
    - A nous de choisir le format des données. Priorité au fonctionnel dans un premier temps !
- "Confirmation de la validité des nouvelles données" : Qu'entendez vous par là ?
    - Pas de format spécial pour le mot de passe. Pour l'adresse email -> format valide. 
    - Pour les champs 'text', chiffres interdits !
- Importance dans la documentation ? 
- Champs obligatoires dans les données de l'utilisateur ?
    - Au moins un nom d'utilisateur et un pwd. Le reste peut-être vide.
- "Un seul utilisateur peut être connecté à tout moment" : Qu'entendez vous par là ?
    - Pas de multi-session
    - Connexion/Deconnexion

### Histoire 2
**Découpement des tâches:**
- créer projet : 8pts
    - attribuer un nom/description/date de fin/ etiquette
    - Visuel :
        - seul le nom doit être visible
        - possibilité d’afficher la description et les étiquettes
- ajouter au projet : 8pts
    - sous-projet : 
        - hériter des étiquettes du projet
    - tâche :
        - simple description
- mode édition : 8pts
    - modification :
        - projet 
        - tâche
    - supp une tâche ou un projet 
- Visuel général : 3pts
- gestion étiquette (associé à un user) : 3pts 


:question: **Question:**  
- Etiquettes personnalisées ? Prédéfinies ? 
    - L'utilisateur peut créer/réutiliser des étiquettes.
    - Uniquement disponible sur le profil de l'utilisateur qui l'a créé.
- Les attributs sont-ils exhaustifs ? 
    - Oui, pour le moment on n'en rajoute pas.


### Histoire 3 (Nécessite histoires 2 pour être dev)
**Découpement des tâches:**  
- Attribuer une date de début/fin à une tâche : 10pts
	- Calendrier pour choisir une date
	- En spécifiant la date de début et la durée de la tâche.
- Classification des tâches par date : 5pts

:question: **Question:**  


### Histoire 4
**Découpement des tâches:**  

- Afficher les projets de l'utilisateur : 6pts
	- Changer la couleur du projet
	- Choisir les projets à afficher
	- Choisir ce qui est affiché dans la tâche

- Afficher le calendrier des tâches (calendarfx) : 14pts
	- Afficher les tâches d'un projet selon leurs dates de début/fin.
	- Afficher les tâches d'un projet selon la couleur associée au projet.
	- Afficher uniquement les informations voulues pr l'utilisateur (au minimum le titre de la tâche)
	
:question: **Question:**  
- Quand on associe une couleur à un projet, cela doit être permanent ?

### Histoire 5 
**Découpement des tâches:**  
(- Structure : 6pts

- Gestion du visuel de toutes les actions de versionning : 10pts

- Add : 4pts -> 0
- Remove : 4pts -> 0
- Branch : 4pts -> 0
- Commit : 4pts -> 0
- Revert : 6pts -> 0
- Merge : 22pts (gestion complète des conflits) -> 8 pts
- Diff : 4pts (simple indication de la position des conflits à résoudre)
- )
- Finir le merge et la gestion de conflits : 8pts



:question: **Question:**    
- Comment différencier les différentes versions ? 
- Pourquoi il n'y a pas de push ? 
- Quelle différence entre un delete de versionning et un delete directement dans l'application ? Est-ce que c'est un delete de back-up ? 
- Le versionning est-il géré en local ou en ligne ? 
- Qu'avez-vous envie de brancher ? A quoi cela vous servirait-il ? 
- Lors d'un revert, faut il supprimer la version "abandonnée" ou l'enregistrer quelque part ? 
- Comment gérer la résolution de conflit dans le merge ? Annulation ? Demande de résolution ? Pause ? 
- Qu'est-ce que vous avez envie de merge ? Des différentes versions d'un projet ? Des projets entre eux (auquel cas, qu'est ce que ça veut dire de merge 2 projets ?) ? 
- Comment doit être géré la gestion des collaborateurs lors d'un merge ? 
- Grande importance du visuel pour la différence ? 


### Histoire 6 (Nécessite histoires 1 et 2)
**Découpement des tâches:**  

- Ajouter/Retirer un utilisateur à un projet en tant que collaborateur 10pts
- Pop-up accepter/refuser la collaboration 6pts
- Pop-up nouvelle tâche attribuée 6pts
- Attribuer une tâche à un collaborateur du projet 4pts
- Filtrer le calendrier des tâches par 'mes tâches/mes tâches attribuées' 6pts

:question: **Question:**     
- Notification au project owner lors d'une acceptation/refus de collaboration ?
	- Les deux côtés doivent être informés, la proposition + acceptation.

### Histoire 7 
**Découpement des tâches:** 

- Exporter un projet : 6pts
- Importer un projet : 6pts
- Compression des données sous format tar.gz 3pts

:question: **Question:**  
- Comment afficher un projet importer ? Différence avec un autre projet ?

### Histoire 8
**Découpement des tâches:**  

- Visualisation des données importantes: 10pts
	- Extraction des données
	- Mise en forme
- Données d'un projet specifique et global: 4pts
- Exportation des données: 4pts

:question: **Question:** 

### Histoire 9 (Nécessite 7)
**Découpement des tâches:**  

- Recherche des services disponibles et de leur api: 6pts
- Gestion du stockage local: 10pts
- Exportation vers services web: 6pts
- Importation d'un services web: 6pts
- Scan du systeme pour éviter un fichier doublon (cfr question): 12pts

:question: **Question:**
- C'est qui l'admin ? (Nous, admin de l'ordi ou créateur du projet)
	- Nous sommes l'admin, nous décidons la quantité d'espace disque allouée
- L'utilisateur choisit le site de stockage ? 
	- 2 services au minimum, l'utilisateur choisit.
- Exportation automatique ou volontaire ?  
- Nos fichiers exportés doivent ils être lisibles sur le cloud ou bien juste être lisible dans le lecteur ?
- Qu'est ce que le système ? (Dossier prédefini de l'app ou système entier)
	- Folder où l'on stocke les projets.

### Histoire 10
**Découpement des tâches:**  

- Rappel interne à l'app: 6pts
- Rappel externe à l'app: 10pts (comprend recherche d'API)
- Gestion des rappels: 4pts

:question: **Question:**  

- Combien de temps avant la fin de la tache pour un rappel ? Fixé par l'utilisateur ?

### Histoire 11
**Découpement des tâches:**  

- View, design: 5pts
- Rédaction de l'aide: 5pts
- Tooltip: 4pts


:question: **Question:** 
- Difficulté d'estimation sans explications supplémentaires des fonctions d'aides demandées !
- Quel genre de tutoriel ? (vidéo, interaction, etc...)

### Histoire 12
**Découpement des tâches:**  

- Sauvegarde des données de manière confidentielle: 12pts
- Cryptage des fichiers/exports: 10pts
- Importation des fichiers crypté: 6pts
- Application d'un mot de passe sur un projet spécifique : 6 pts

:question: **Question:**  
- Quel niveau de sécurité est demandé ? 
- Quels fichiers sont cryptés ?

### Histoire 13
**Découpement des tâches:**  

- Recherche des méthodes de vérification des données: 10pts
- Erreur checks sur les importations: 10pts
- Vérification de l'intégrité de la BDD: 10pts
- Verification de données geet: 10 pts

:question: **Question:**  

- Quelles données sont à vérifier ?
- Comment vérifier l'intégrité de données ? 
- Que faire si une faile de sécurité s'est produite ? 
- Nous voulons plus d'explications ! 
