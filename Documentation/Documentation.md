#What 2 Watch - Documentation technique
***
1. Technologies

    1.1 Librairies externes (JSON, SQLite etc..)

2. Fonctionnalités de l'application

3. Architecture: schéma + explication du fonctionnement de l'application

4. Reprise de projet
    - Environnement de développement

5. Conclusion



## 1.Technologies

  ###1.1 Java
- Premier langage
- Multi plateforme
- Simplicité de création d'interfaces
- Présence de Java dans de titrebreux systèmes

  ###1.2 Bibliothèques externes
  **SQLite**

  Sqlite est une bibliothèque écrite en C qui propose un moteur de base de données relationnelle accessible par le langage SQL.

  Nous avons choisi d'intégrer cette bibliothèque car elle permet de ne pas reproduire un schéma habituel de client - serveur, mais d'être directement intégrée au programme où l'intégralité de la base de données est stockés dans un fichier indépendant de la plateforme. Ce qui permet d'une part de pouvoir la consulter et d'y faire des opération sans avoir besoin d'une connexion internet et d'une autre part de profiter de son fonctionnement multiplateforme.

  De plus, SQLite est une bibliothèque open source et extrêmement légère. C'est pour cela que c'est le moteur de base de données le plus utilisé au monde, surtout dans des logiciels grand public ou des systèmes embarqués. 

  SQLite nous paraît donc très adapté pour ce genre de projet qui de plus ne demandera pas une gestion d'une immense quantité de films.

  Liens vers la bibliothèque : [SQLite] (https://bitbucket.org/xerial/sqlite-jdbc)

 **JSON-java**

  JSON-java est une bibliothèque open source et populaire qui permet de décoder / encoder des fichiers JSON.

  Nous avons choisi d'intégrer cette bibliothèque car elle est très légère et son utilisation est très simple.

  Dans notre programme, nous devons décoder à plusieurs reprises des fichier JSON qui nous sont retournés par l'API, de ce fait nous avons pensé utile de l'utiliser.

  Liens vers la bibliothèque : [JSON-java] (https://github.com/stleary/JSON-java)


## 2.Fonctionnalités de l'application
- recherche de fichiers (raph)

**analyse des fichiers, récupération du titre du film**

Pour que l'API puisse nous retourner les données du film que l'ont veut, il faut pouvoir lui passer le titre du film le plus épuré possible. Voivi donc comment nous procédons:

Nous avons une première fonction qui va faire un remplacement par un espace de tout ce que l'on rentre à la main comme pattern, par exemple des termes comme : Xvid, bdrip, VOSTFR, etc. ainsi que des ponctuations : ".-_())". Nous pouvons donc l'affiner au fur et à mesure.

Une seconde fonction va permettre de remplacer les possibles dates qu'elle trouve, par un espace.

Note : vu qu'il y a forcément un espace ou un caractère séparateur (qui sera transformer en espace) entre les divers mots de la chaine de caractère, au moment ou l'on remplace nos pattern trouvé par un espace, cela créera forcément au minimum 2 espaces.

une dernière fonction, que nous pensons la plus intéressante, va découper l'ensemble de la chaîne de caractère (après le passage dans la fonction 1 et 2) par tranche de double espace, pour en faire un tableau. Nous allons ensuite parcourir ce tableau et quand on va trouvé une case du tableau qui contient des caractères, nous la gardons car cela sera normalement le titre du film. Avec cette fonction, il suffit donc de trouver les patterns critiques soit : devant le titre du films (si il y en a), ou un seul qui est juste derrière le titre du film, et tous les autres pattern qui suivent ne seront même pas pris en compte, ce qui est interessant.

Exemple :
Nous avons le film : Colt.45.2014.FRENCH.BRRiP.XviD-CARPEDIEM
Après le passage dans la fonction 1 et 2, il restera : Colt 45    BRRiP   CARPEDIEM
tab[1] = "Colt 45", tab[2] = "", tab[3] = "BRRiP", tab[4] = " CARPEDIEM"
Nous allons donc récupérer la première case non vide (donc le titre du film)

Si il y avait eu un pattern devant le titre du film, cela fonctionne toujours (du moment qu'il est transformé en espace par notre RegEx) car la première case du tableau aurait été vide et celle d'après serait le titre du film.

Pour résumer, cette fonction est bien sûr dépendante de notre fonction 1 (RegEx), mais si l'on arrive à transformer un des patternes critique (ceux devant le titre [si il y en a] sinon, celui qui suit directement le titre), nous arrivons à récupérer le titre du film.

- récupération d'infos (Loïc)
- Recherche de films (raph)
- UI (raph)
- Offline, lié au choix de sqlite (loïc)




#W2W
- Interface utilisateur
    - Vue d'option
    - Vue principale
- Parcours de dossiers recursif
    - Exclusion des séries
- Recherche
