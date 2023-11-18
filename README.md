# Sokoban
Résolution d'un Sokoban sous forme d'un problème PDDL à partir du projet disponible ici : https://github.com/fiorinoh/sokoban
Le git ci-dessus propose différents niveaux du jeu Sokoban (dossier config). Ils sont convertis en problèmes PDDL avec ProblemBuilder.java, résolus grâce à pddl4j et au fichier domain.pddl, puis les actions sont générés par Agent.java pour l'affichage graphique dans une page html.

Mise en place :
- Cloner le projet et installer pddl4j sur le git suivant : https://github.com/fiorinoh/sokoban
- Placer les 3 fichiers de ce git dans le projet précédemment cloné à cet endroit : sokoban/src/main/java/sokoban
- Ouvrir le fichier sokoban/src/main/java/sokoban/SokobanMain.java et modifier la valeur de la variable levelNumber (à la ligne 16) par un nombre de 1 à 30
- Le nombre correspond au niveau que l'on souhaite résoudre (ex : pour resoudre test21.json, on met la valeur 21)
- Avec l'invité de commande, se placer dans le dossier sokoban
- Faire la commande : mvn package
- Faire la commande : java --add-opens java.base/java.lang=ALL-UNNAMED -server -Xms2048m -Xmx2048m -cp target/sokoban-1.0-SNAPSHOT-jar-with-dependencies.jar sokoban.SokobanMain
- Ouvrir http://localhost:8888/test.html dans un navigateur
- Deux fichiers sont crées dans src/main/java/sokoban: 
	- problem21.pddl (si on a choisi le niveau 21) qui contient le problème pddl généré pour le niveau
	- solution.txt qui contient la chaine de caractère lue par l'agent pour la résolution graphique
- Lorsque l'on appuie sur play, le Sokoban est résolu graphiquement
