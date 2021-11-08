# Way t0 3xcel : Projet de génie logiciel et gestion de projet (INFO-F-307)

Le but de ce projet est de développer une application cross-plateforme de gestion de projets. Toute ressemblance avec des logiciels existants est fortuite.

# Utilisation

Le programme utilise JAVA 1.8 ainsi que maven pour gérer la dépendances des librairies utilisées. Aucune installation n'est nécessaire au bon fonctionnement du programme. Par ailleurs, nous utilisons de nombreuses librairies notamment:
- calendarFX
- gson
- sqlite-jdbc
- dropbox-core
- google api
- jupiter
- jarchivelib
- spring-security-crypto
- bcrypt

Le programme est aussi utilisable sur tous les sytèmes d'exploitation actuels (macOS, windows, linux).

Si vous avez été un de nos beta testeurs, certains fichiers doivent absoluments être effacés pour le bon fonctionnement depuis la release 1.0.0 du 10/05/2021. Ces fichiers sont : MyDataBase.db, le dossier token contenant les tokens lien l'application aux api de cloud et de calendrier, et le fichier TestDataBase.db pour que les tests tournent correctement. Il est recommandé de complètement clone à nouveau le repository dans un nouveau dossier. 

## Compilation

Aucune compilation n'est nécessaire pour exécuter le programme. Un exécutable JAR est fourni. Si vous souhaitez toutefois manipuler le code source, une version 1.8 de java et recourir à notre fichier pom.xml sous maven suffira à ce que la compilation se passe sans accro.

## Démarrage 

Double-cliquer sur l'exécutable JAR, vous pourrez alors créer un compte et utiliser toutes les fonctionnalités du programme.

# Configuration :

Nous utilisons intellij afin de développer notre programme avec corretto-1.8 en SDK du projet. Il est aussi nécessaire de marquer le dossier ´src´ comme `source root` et le dossier `test` comme `test source root`.
![image](https://user-images.githubusercontent.com/43049559/112755228-10121980-8fe0-11eb-990d-4dbf097866b9.png)
![image](https://user-images.githubusercontent.com/43049559/112755232-156f6400-8fe0-11eb-8a9c-5e4d663b4187.png)

 Par ailleurs, il est obligatoire d'utiliser maven afin d'exécuter le programme depuis intellij. Cependant, il faut bien faire attention à `reload all maven projects` ![image](https://user-images.githubusercontent.com/43049559/112755304-5e271d00-8fe0-11eb-83d0-a06ced2b1751.png)

# Tests
Si une ancienne version de TestDatabase.db existe localement sur la machine faisant tourner le programme, il est nécessaire de la supprimer pour que les tests runs proprement. Si la database a été créée sur la dernière version on date, il n'y a jamais aucun problème à faire tourner les tests. 
Il suffit de clique-droit sur le dossier test et de sélectionner `run all tests` afin d'exécuter tous les tests.
![image](https://user-images.githubusercontent.com/43049559/112755358-96c6f680-8fe0-11eb-9d29-63aeb78bd472.png)


## Screenshot

### Launching view
![image](https://user-images.githubusercontent.com/33007350/116140534-bfbdd280-a6d7-11eb-90b0-df68608ad77f.png)
### Main view
![image](https://user-images.githubusercontent.com/33007350/117466360-e8a15b80-af52-11eb-8abc-cc0606ae0d8a.png)
### Edit project view
![image](https://user-images.githubusercontent.com/33007350/116142122-cf3e1b00-a6d9-11eb-851f-f57098f47fc3.png)
### Calendar view
![image](https://user-images.githubusercontent.com/33007350/116141454-fb0cd100-a6d8-11eb-802c-23e09fe0ec85.png)
### Task view
![image](https://user-images.githubusercontent.com/33007350/117466675-4766d500-af53-11eb-9a06-529b859ce47c.png)
### Stat view
![image](https://user-images.githubusercontent.com/33007350/117466957-8ac14380-af53-11eb-8df4-976a574ce1ac.png)



## Remerciements
# Toute l'équipe du groupe 03 vous remercie pour votre intérêt pour Way T0 3xcel ! 
![image](https://cdn.discordapp.com/attachments/807233727309676618/840234348455919616/unknown.png)

## License
CC0 1.0 Universal
