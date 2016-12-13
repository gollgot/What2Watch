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
- Présence de Java dans de nombreux systèmes

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
- analyse de noms de fichiers (Loïc)
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
