# What 2 Watch - Documentation technique

[Repository GitHub] (https://github.com/raph-u/What2Watch/)


**OU METTRE UI ? Raph**

## Sommaire

1. Architecture

2. Language

3.  Bibliothèques externes

    3.1 SQLite

    3.2 JSON-java

4. Fonctionnalités importantes

    4.1 Recherche de fichiers

    4.2 Magic Numbers

    4.3 Analyse des fichiers, récupération du titre du film

    4.4 API: récupération des informations

    4.5 Utilisation "offline" de l'application

5. Installation de What2Watch

    5.1 JDK

    5.2 Application What2Watch

    5.2 Lancement de l'application

6. Reprise du projet

    6.1 GitHub

    6.2 JDK

    6.3 IDE

    6.4 API Key

7. Conclusion

---

## 1. Architecture

À compléter

---

## 2. Language

À compléter

Java
- Premier langage
- Multi plateforme
- Simplicité de création d'interfaces
- Présence de Java dans de titrebreux systèmes

---
### 3.  Bibliothèques externes

Nous avons choisi d'utiliser deux bibliothèques externes, voici lesquelles ont été choisi et pour quelle raison.

#### 3.1 SQLite

Sqlite est une bibliothèque écrite en C qui propose un moteur de base de données relationnelle accessible par le langage SQL.

Nous avons choisi d'intégrer cette bibliothèque car elle permet de ne pas reproduire un schéma habituel de client - serveur, mais d'être directement intégrée au programme où l'intégralité de la base de données est stockés dans un fichier indépendant de la plateforme. Ce qui permet d'une part de pouvoir la consulter et d'y faire des opération sans avoir besoin d'une connexion internet et d'une autre part de profiter de son fonctionnement multiplateforme.

De plus, SQLite est une bibliothèque open source et extrêmement légère. C'est pour cela que c'est le moteur de base de données le plus utilisé au monde, surtout dans des logiciels grand public ou des systèmes embarqués.

SQLite nous paraît donc très adapté pour ce genre de projet qui de plus ne demandera pas une gestion d'une immense quantité de films.

Dans notre projet, vous pouvez retrouver la base de données qui est à l'emplacement : `cache/cache.db`. Nous avons décider de l'appelée ainsi, car elle fait vaiment office de cache, l'utilisateur peut la supprimer si elle serait corrompue, ou même par inadvertance et lors du lancement de l'application elle sera recrée automatiquement dans le dossier cache.

Liens vers la bibliothèque : [SQLite] (https://bitbucket.org/xerial/sqlite-jdbc)

#### 3.2 JSON-java

JSON-java est une bibliothèque open source et populaire qui permet de décoder / encoder des fichiers JSON.

Nous avons choisi d'intégrer cette bibliothèque car elle est très légère et son utilisation est très simple.

Dans notre programme, nous devons décoder à plusieurs reprises des fichier JSON qui nous sont retournés par l'API, de ce fait nous avons pensé utile de l'utiliser.

Liens vers la bibliothèque : [JSON-java] (https://github.com/stleary/JSON-java)

---

### 4. Fonctionnalités importantes

Nous allons vous expliquer le fonctionnement des fonctionnalités que nous pensons être les plus importantes et pertinentes.

Les informations porteront plus sur la logique en elle-même et non l'explication du code. Pour voir le code source, vous pouvez le retrouver à l'emplacement : `What2Watch/src/what2watch`.

#### 4.1 Recherche de fichiers

À compléter

#### 4.2 Magic Numbers

À compléter

#### 4.3 Analyse des fichiers, récupération du titre du film

Pour que l'API puisse nous retourner les données du film que l'ont veut, il faut pouvoir lui passer le titre du film le plus épuré possible. Voici donc comment nous procédons:

Nous avons une première fonction qui va faire un remplacement par un espace de tout ce que l'on rentre à la main comme pattern, par exemple des termes comme : Xvid, bdrip, VOSTFR, etc. ainsi que des ponctuations : ".-\_()". Nous pouvons donc l'affiner au fur et à mesure.

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

#### 4.4 API: récupération des informations

Concernant la récupération d'informations sur un film, nous utilisons l'API du site "www.themoviedb.org" : liens vers la documentation de l'API : [TMDB API] (https://developers.themoviedb.org/3/getting-started).
Pour utiliser cette API nous devons utiliser une clef, nous avons donc stocké cette clef dans un fichier .env qui n'est pas à disposition dans le repository (question de sécurité durant la phase de développement).

Nous somme également limité au niveau du nombre de requête par seconde, ce qui implique une certaine attente supérieure à celle que nous aurions voulu au départ. Mais nous avons quand même décidé d'utiliser cette API car elle apporte tous ce que nous cherchons. Nous lui envoyons le titre du film (autant en Français qu'en Anglais) dont nous recherchons les informations, et l'API nous retourne tous ce dont nous avons besoin, en format JSON et dans la langue souhaitée. Il nous reste donc plus qu'à parser le tout et à l'afficher dans notre application.

#### 4.5 Utilisation "offline" de l'application

Un point que nous trouvons très intéressant et important dans notre application, est le fait qu'elle soit utilisable sans avoir de connection internet.

Avoir choisi d'utiliser une base de données en SQLite nous permet de pouvoir la consulter à n'importe quel moment, sans être dépendant d'une connection internet. De ce fait, du moment que l'utilisateur à lancé au moins une fois la recherche d'informations en ayant internet, il est possible de récupérer toutes les informations de ces films autant de fois qu'il le veut, sans avoir internet. Par contre il sera donc impossible de récupérer les informations de nouveaux films ajoutés dans son dossier sans avoir internet.

Nous avons donc mis en place toute une gestion de la connection à internet. Nous avons une méthode qui permet d'ouvrir un socket vers le site "www.google.com" et qui nous retourne true ou false, respectivement: internet accessible ou non. Nous regardons donc à chaque début de scan si l'utilisateur à accès à internet ou non. Dans le cas ou il ne l'a pas, nous affichons on message d'alert. Notre application gère aussi le cas ou il serait possible que l'utilisateur perde la connection au milieu d'une recherche. Si cela arrive, toutes les données non-trouvées seront égale à "Unknown" et l'application affiche un message d'alert.

Nous avons de plus intégré en bas à droite de l'application, un voyant vert ou rouge qui permet d'indiquer à l'utilisateur si il a une connection internet ou pas. Au niveau du code, c'est un thread lancé au lancement de l'application et qui vérifie chaque seconde si internet est accessible ou pas. Ce thread se ferme correctement lorsque l'application se ferme.

---

### 5. Installation de What2Watch

Voici toutes les étapes à suivre pour installer notre application sur votre ordinateur. Que vous soyez sur Windows, Mac OS ou Linux il n'y aura aucun problème, car l'application est entièrement multiplateforme.


#### 5.1 JRE

Pour pouvoir exécuter des programmes Java (.jar) vous êtes obligé d'installer le JRE (Java SE Runtime Environment). Il vous faut obligatoirement le JRE 8u111.

*Note : Si vous avaz déjà Java 8 d'installer vu pouvez sauter cette étape.*

Pour voir la version de java installer :

- Sous Windows : Aller dans le panneau de configuration -> Programmes -> Programmes et fonctionnalités. Puis rechercher Java et vous allez voir la version installée.
- Sous Unix : Ouvrez un terminal puis tapper la commande : `java -version`. Si vous avez un message d'erreur c'est que java n'est pas installé, sinon il faut que vous ayez un message : "Java version 1.8.x".

Téléchargement du JRE : 

Voici le lien de téléchargement : [JRE 8u111] (http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) 

Il suffit de télécharger et installer le JRE 8u111 prévu pour l'OS de votre choix.

Pour l'installation en ligne de commande via Linux Ubuntu et Linux Debian, voici deux très bon tutoriels (c'est pour installer le JDK 8, qui intègre lui-même le JRE 8):
- [Ubuntu] (http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)
- [Debian] (http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html)

#### 5.2 Application What2Watch

Pour installer notre application sur votre ordinateur, que vous ayez Windows, Mac OS ou Linux, c'est le même procédé. Il faut télécharger l'archive "What2Watch-deployment.zip" qui se trouve sur [GitHub](https://github.com/raph-u/What2Watch/tree/deployment). Pour cela cliquez simplement sur le bouton "Clone or download" et choisir "Download ZIP". 

Une fois l'archive extraite, vous trouverez à l'intérieur un dossier "What2Watch" vous pouvez le placer ou vous le voulez (par exemple dans votre dossier de programmes).

À l'intérieur du dossier, vous trouverez un fichier nommer "What2Watch.jar". Ce fichier est le fichier à exécuter pour lancer l'application.

L'ensemble des fichiers / dossiers présent dans le dossier What2Watch doivent y rester, si vous voulez avoir le fichier What2Watch.jar par exemple sur votre bureau, il suffit d'en créer un raccourci, puis vous pouvez placer ce raccourci ou bon vous semble.

#### 5.2 Lancement de l'application

Si vous avez une interface graphique : 
- Ouvrez le fichier "What2Watch.jar" en double cliquant dessus. (Si le fichier s'ouvre pas, veuillez l'ouvrir avec le programme : "Java(TM) Platform SE binary").

Si vous utilisez un terminal :
- Lancer votre terminal préféré et tapper la commande : `java -jar <nom_du_fichier.jar>`

---

### 6. Reprise du projet

Nous avons découpé la reprise du projet en plusieurs étapes, pour pouvoir les expliquer comme il faut. Il suffit de suivre les points un par un et vous pourrez reprendre le projet sans aucun problème.

#### 6.1 GitHub

L'ensemble du projet est disponible sur GitHub : [What2Watch] (https://github.com/raph-u/What2Watch)

Vous pouvez donc récupérer l'ensemble du repository et par exemple l'ouvrir avec un outil de versionning (nous avons utilisé GitKraken).

Ou récupérer la branche de votre choix, pour ensuite en modifier le contenu.

Voici comment notre système de branches fonctionne :

- **Master** : Cette branche contient uniquement des versions livrables du projet.
- **Develop** : Cette branche contient des versions stables du projet, mais toujours en cours de développement.
- **Toutes les autres branches** : Pour chaque grande fonctionnalité (généralement plus de un ou deux commits) nous avons créé une branche spécifique à celle-ci et travaillions dessus jusqu'à avoir une version stable et ensuite faire un merge sur la branche develop.

#### 6.2 JDK

Nous avons utilisé le JDK 8u111 pour complilé notre application. Il est donc impératif de prendre la même version. Voici le lien de téléchargement : [JDK 8u111] (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 

Il suffit de télécharger et installer le JDK 8u111 prévu pour l'OS de votre choix.

Pour l'installation en ligne de commande via Linux Ubuntu et Linux Debian, voici deux très bon tutoriels :
- [Ubuntu] (http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)
- [Debian] (http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html)

#### 6.3 IDE

Vous pouvez utiliser n'importe quel IDE qui permet de reprendre un projet Java, mais nous vous recommandons tout de même d'utiliser le même que nous qui est Netbeans (version 8.2 ).

Liens de téléchargement : [Netbeans] (https://netbeans.org/downloads/)

*Note : Ne pas oublié d'indiquer le chemin vers le JDK installé au point "6.2 JDK" lors de l'installation*

Il ne vous reste donc plus qu'à ouvrir le projet Java de la branche que vous voulez (téléchargé au préalable, voir point "6.1 GitHub"). Exemple : `What2Watch-develop/What2Watch`

Si vous utilisez Netbeans, il n'y a rien d'autre à faire. Toutes les bibliothèques sont déjà liées au projet etc. Si vous utilisez un autre IDE, il se peut qu'il faudra lié les bibliothèques au projet. Vous pouvez les retrouvées dans le dossier `What2Watch/libraries`.

#### 6.4 API Key

Notre application utilise une clef d'API et pour ne pas qu'elle se retrouve publiquement sur le repository, nous avons décider de la stocker dans un fichier ".env" lui-même stocké sur notre ordinateur (le fichier .env est ignoré lors des commits grâce au fichier .gitignore).

D'abbord, vous devez récupérer votre propre clef d'API sur le site [The Movie DB](https://www.themoviedb.org/) car nous ne fournissons pas la clef d'API avec notre application. Pour cela il suffit de s'incrire sur le site et de demander une API KEY, c'est entièrement gratuit et instantané.

Ensuite, il faut que vous créiez un fichier ".env" dans le dossier du projet What2Watch (à la même hauteur que le dossier "src"). À l'intérieur de ce fichier, il faut écrire cette ligne : `api_key:xxxxxxxxxxxxxxxxx` en remplaçant les "x" par votre clef.

Pour finir, il faut que vous sachiez encore une chose, aller dans le code source de la classe "ApiHandler". Il y a un attribut de classe qui s'appelle "apiKey", sa valeure est égale à "getApiKey()". C'est une méthode de classe qui va permettre d'aller récupérer le contenu du fichier ".env" et donc de récupérer la valeur de la clef d'API. Si vous déployez votre application, n'oubliez pas de changer la valeur de l'attribut "apiKey" par la valeur de la clef d'API et non par la méthode, car le fichier ".env" ne sera pas disponible chez l'utilisateur.

---

### 7. Conclusion

À compléter 

---








#W2W
- Interface utilisateur
    - Vue d'option
    - Vue principale
- Parcours de dossiers recursif
    - Exclusion des séries
- Recherche
