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

  Dans notre projet, vous pouvez retrouver la base de données qui est à l'emplacement : `cache/cache.db`. Nous avons décider de l'appelée ainsi, car elle fait vaiment office de cache, l'utilisateur peut la supprimer si elle serait corrompue, ou même par inadvertance et lors du lancement de l'application elle sera recrée automatiquement dans le dossier cache.

  Liens vers la bibliothèque : [SQLite] (https://bitbucket.org/xerial/sqlite-jdbc)

 **JSON-java**

  JSON-java est une bibliothèque open source et populaire qui permet de décoder / encoder des fichiers JSON.

  Nous avons choisi d'intégrer cette bibliothèque car elle est très légère et son utilisation est très simple.

  Dans notre programme, nous devons décoder à plusieurs reprises des fichier JSON qui nous sont retournés par l'API, de ce fait nous avons pensé utile de l'utiliser.

  Liens vers la bibliothèque : [JSON-java] (https://github.com/stleary/JSON-java)


## 2.Fonctionnalités de l'application
- recherche de fichiers (raph)

**analyse des fichiers, récupération du titre du film**

Pour que l'API puisse nous retourner les données du film que l'ont veut, il faut pouvoir lui passer le titre du film le plus épuré possible. Voici donc comment nous procédons:

Nous avons une première fonction qui va faire un remplacement par un espace de tout ce que l'on rentre à la main comme pattern, par exemple des termes comme : Xvid, bdrip, VOSTFR, etc. ainsi que des ponctuations : ".-_()". Nous pouvons donc l'affiner au fur et à mesure.

Une seconde fonction va permettre de remplacer les possibles dates qu'elle trouve, par un espace.

*Note : vu qu'il y a forcément un espace ou un caractère séparateur (qui sera transformer en espace) entre les divers mots de la chaine de caractère, au moment ou l'on remplace nos pattern trouvé par un espace, cela créera forcément au minimum 2 espaces.*

une dernière fonction, que nous pensons la plus intéressante, va découper l'ensemble de la chaîne de caractère (après le passage dans la fonction 1 et 2 par tranche de double espace, pour en faire un tableau. Nous allons ensuite parcourir ce tableau et quand on va trouvé une case du tableau qui contient des caractères, nous la gardons car cela sera normalement le titre du film. Avec cette fonction, il suffit donc de trouver les patterns critiques soit : devant le titre du film (si il y en a), ou un seul qui est juste derrière le titre du film, et tous les autres pattern qui suivent ne seront même pas pris en compte, ce qui est vraiment interessant.

*Exemple :
Nous avons le film : Colt.45.2014.FRENCH.BRRiP.XviD-CARPEDIEM
Après le passage dans la fonction 1 et 2, il restera : Colt 45 (4 espaces) BRRiP (3 espaces) CARPEDIEM. Donc => 
tab[1] = "Colt 45", tab[2] = "", tab[3] = "BRRiP", tab[4] = " CARPEDIEM"
Nous allons donc récupérer la première case non vide (donc le titre du film)*

Si il y avait eu un pattern devant le titre du film, cela fonctionne toujours (du moment qu'il est transformé en espace par notre RegEx) car la première case du tableau aurait été vide et celle d'après serait le titre du film.

Pour résumer, cette fonction est bien sûr dépendante de notre fonction 1 (RegEx), mais si l'on arrive à transformer un des patternes critique (ceux devant le titre [si il y en a] sinon, celui qui suit directement le titre), nous arrivons à récupérer le titre du film sans se soucier des autres patternes qui suivent.

**API: récupération des informations**

Concernant la récupération d'informations sur un film, nous utilisons l'API du site "www.themoviedb.org" : liens vers la documentation de l'API : [TMDB API] (https://developers.themoviedb.org/3/getting-started).
Pour utiliser cette API nous devons utiliser une clef, nous avons donc stocké cette clef dans un fichier .env qui n'est pas à disposition dans le repository (question de sécurité durant la phase de développement). 

Nous somme également limité au niveau du nombre de requête par seconde, ce qui implique une certaine attente supérieure à celle que nous aurions voulu au départ. Mais nous avons quand même décidé d'utiliser cette API car elle apporte tous ce que nous cherchons. Nous lui envoyons le titre du film (autant en Français qu'en Anglais) dont nous recherchons les informations, et l'API nous retourne tous ce dont nous avons besoin, en format JSON et dans la langue souhaitée. Il nous reste donc plus qu'à parser le tout et à l'afficher dans notre application.

- Recherche de films (raph)
- UI (raph)

**Utilisation "offline" de l'application** 

Un point que nous trouvons très intéressant et important dans notre application, est le fait qu'elle soit utilisable sans avoir de connection internet.

Avoir choisi d'utiliser une base de données en SQLite nous permet de pouvoir la consulter à n'importe quel moment, sans être dépendant d'une connection internet. De ce fait, du moment que l'utilisateur à lancé au moins une fois la recherche d'informations en ayant internet, il est possible de récupérer toutes les informations de ces films autant de fois qu'il le veut, sans avoir internet. Par contre il sera donc impossible de récupérer les informations de nouveaux films ajoutés dans son dossier sans avoir internet.

Nous avons donc mis en place toute une gestion de la connection à internet. Nous avons une méthode qui permet d'ouvrir un socket vers le site "www.google.com" et qui nous retourne true ou false, respectivement: internet accessible ou non. Nous regardons donc à chaque début de scan si l'utilisateur à accès à internet ou non. Dans le cas ou il ne l'a pas, nous affichons on message d'alert. Notre application gère aussi le cas ou il serait possible que l'utilisateur perde la connection au milieu d'une recherche. Si cela arrive, toutes les données non-trouvées seront égale à "Unknown" et l'application affiche un message d'alert.

Nous avons de plus intégré en bas à droite de l'application, un voyant vert ou rouge qui permet d'indiquer à l'utilisateur si il a une connection internet ou pas. Au niveau du code, c'est un thread lancé au lancement de l'application et qui vérifie chaque seconde si internet est accessible ou pas. Ce thread se ferme correctement lorsque l'application se ferme.


#W2W
- Interface utilisateur
    - Vue d'option
    - Vue principale
- Parcours de dossiers recursif
    - Exclusion des séries
- Recherche
