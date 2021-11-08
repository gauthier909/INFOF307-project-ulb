# Examen
[EN] Here are your questions for the INFO-F-307 exam.

To answer them, discuss with each other (on the platform of your choice) in order to provide the best answer possible.

For each question, answer in a clear, concise and complete manner, using the method that seems most appropriate to you, namely:
  - by replying in this file
  - by annotating / modifying your files
  	- for example, if a question concerns a wrong code, you can suggest a correction
  - by adding other files (PDF, ...)
  - by putting a link to a video of you answering us orally
  - ...

Whichever way you use it, if you don't answer a question directly in this file, clearly describe after that question where the answer can be found (for example, "see the Controller.java file").

Before 11am, once you think you've finished answering our questions, commit and push your changes on the git.

[FR] Voici vos questions pour l'examen d'INFO-F-307.

Pour y répondre, concertez-vous (via le moyen de votre choix) pour répondre au mieux à chaque question.

Pour chaque question, répondez de manière claire, concise et complète, en utilisant la méthode qui vous paraît la plus appropriée, à savoir:
  - en répondant dans ce fichier
  - en annotant/modifiant vos fichiers
    - par exemple, si une question porte sur un mauvais code, vous pouvez proposer une correction
  - en ajoutant d'autres fichiers (PDF, ...)
  - en mettant un lien vers une vidéo de vous nous répondant oralement
  - ...

Qu'importe la manière utilisée, si vous ne répondez pas directement à une question dans ce fichier, décrivez clairement après cette question où la réponse peut être trouvée (par exemple, "voir le fichier Controller.java").

Avant 11h, une fois que vous pensez avoir fini de répondre à nos questions, commitez et pushez vos modifications sur le git.

## Question 1
Votre tableau du burndown chart pour I4 a des valeurs négatives. Comment cela est-il possible ? Cela signifie-t-il que vous avez travaillé plus d'heures que prévu ? Si oui, cela répond-il à la méthodologie ?


  L’estimation de temps pour finir l’histoire 5, que nous considérions comme l’histoire la plus difficile parmi toutes les histoires, a été légèrement sous-évaluée. On parle ici d’une sous-évaluation de 4.5 points. Cependant, ces 4.5 points nous ont permis de terminer l’histoire 5 et n’ont pas remis en question l’intégrité des autres histoires. La théorie d’XP voudrait que soit :
 1. Les estimations sont complètement erronées et il faut privilégier la qualité du code en abandonnant certaines histoires,
 2. Qu’on parle avec le client des problèmes de développement en cours pour revoir ses priorités.


Dans ce cas particulier, nous avons bel et bien complété l'entièreté des histoires désirées par le client sans que cela ne dégrade la qualité du code, et le nombre d’heures prestées au total par chaque membre de l’équipe ne dépasse aucunement le quota d’heure fixé par itération (non, nous n’avons pas travaillé plus d’heures que prévu). Cela ne colle pas parfaitement à la méthodologie mais dans le cadre d’une dernière itération, nous avons presté toutes nos heures et n’allions pas laisser un travail inachevé.  


## Question 2
BaseViewController : êtes-vous sûrs que toutes ses méthodes soient nécessaires aux différents ViewController ? Comment refactoriser pour que les responsabilités soient réparties au mieux ?


 Non, toutes ces méthodes ne sont pas nécessaires dans le BaseViewController, en particulier celles qui ne sont présentes que dans un seul Controller. Prenons pour exemple la méthode alertCommitMessage (BaseViewController:61), qui n’est présente que dans le HomeViewController. 

Certaines de ces méthodes, telles que recallProjectAlert (BaseViewController: 107) et recallTaskAlert (BaseViewController: 93) auraient pu être déplacées dans le HomeViewController et changées par un appel vers alertCreatorAndHandler (BaseViewController: 45), qui en deviendrait plus générique.

De plus, pour revenir sur le premier point qui concerne les méthodes présentes seulement dans un seul Controller de vue. Si des méthodes sont utilisées dans deux controlleurs ou au plus trois. Alors on peut considérer que ces méthodes ne sont pas suffisamment génériques, et donc de créer un controller qui hériterait lui-même de BaseViewController et dans lequel nous déplacerions les méthodes concernées, ce qui serait une solution viable. Ce controller serait donc lui-même implémenté par les controllers demandeurs. Afin d’augmenter la clarté et la maintenabilité du code, nous pourrions découper ces héritages en packages. 


## Question 3
La couverture des tests a beaucoup augmenté depuis l'itération 3. Qu'est-ce que cela indique sur le TDD ? A-t-il été respecté depuis le début ? Comment changeriez-vous les choses si vous pouviez recommencer le projet maintenant ? 


Cela indique que l’on n’a pas pris le temps d’écrire systématiquement les tests avant d’écrire les features du projet, par conséquent notre méthodologie initiale n’était pas toujours axée vers du TDD. Cependant durant l’itération 4, nous avons travaillé en TDD.

Si le projet était à refaire, des tests seraient écrits dès la première itération, afin d’éviter de devoir repasser sur du code dans le but d’écrire les tests correspondants, et de détecter des problèmes dans l’application que ces tests peuvent permettre de détecter plus facilement. 


## Question 4
Il y a une énorme différence entre les lignes de code poussées vers le master par les différents contributeurs. Quelqu'un a plus de 60 000 lignes alors qu'un autre en a moins de 500. Comment est-ce possible ? 


  Cela est dû au fait que les lignes que l’on peut voir dans les contributeurs ne prennent en compte que les pushs sur le master. Nous avons travaillé par un système de pull request, ce qui est mal tracké par github. Pour chaque histoire nous avions une ou plusieurs branches, et c’est uniquement la personne qui l’intégrait dans le master qui se voyait attribuer toutes ses lignes. 

De plus, les 60 000 lignes d’un contributeur viennent du fait qu’il ait push la javadoc sur le master. Tout le monde a travaillé de manière équitable dans le groupe.


## Question 5
be/ac/ulb/infof307/g03/models/Project.java:342 : renvoie une map de map. Qu'y a-t-il de mieux à faire ?


  La map d’une map avait pour objectif de pouvoir récupérer visuellement ce que l’on désirait. Donc, par exemple, pouvoir get la key “difference” afin que le code soit directement plus lisible. 

Une solution plus maintenable et visuelle serait de créer un objet Différence qui contiendrait les maps des différences, ajouts et suppressions. 

