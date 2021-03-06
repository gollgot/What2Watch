# What 2 Watch - Documentation technique

- Auteurs: Loïc Dessaules, Raphaël Bazzari
- Date: 22.12.2016
- Language: Java
- Lien vers notre repository : [What2Watch - GitHub](https://github.com/raph-u/What2Watch/)


## Sommaire

1. Architecture

    1.1 Schéma de communication

    1.2 Base de données (MLD)

2. Language

3.  Bibliothèques externes

    3.1 SQLite

    3.2 JSON-java

4. Fonctionnalités importantes

    4.1 Recherche de fichiers

    4.2 Magic Numbers

    4.3 Analyse des fichiers, récupération du titre du film

    4.4 API: récupération des informations

    4.5 Interface utilisateur

    4.6 Utilisation "offline" de l'application

5. Installation de What2Watch

    5.1 JRE

    5.2 Application What2Watch

    5.3 Lancement de l'application

6. Reprise du projet

    6.1 GitHub

    6.2 JDK

    6.3 IDE

    6.4 API Key

7. Bogues restants

8. Améliorations possibles

9. Conclusion

---

## 1. Architecture

#### 1.1 Schéma de communication

![Settings](screenshots/schema.png "W2W - Schéma de communication")

#### 1.2 Base de données (MLD)

![MLD](screenshots/MLD.png "W2W - MLD")

Détail des tables :

 - **movie :** On y retrouve toutes les informations propre à chaque film.

 - **actor, director :** On y retrouve, respectivement, le nom des acteurs et des directeurs présent dans un minimum un film. Pour le nom nous avons volontairement mis le nom et le prénom dans le même champ, car l'API nous retourne par exemple le nom "John Carl Buechler". Il est donc très difficile de savoir quel est le nom et le prénom (ce n'est pas toujours les "deux premières parties qui sont le prénoms").
 
 - **genre :** On y retrouve les genres présents dans au minimum un film.
 
 - **movie_has_actor, movie_has_director, movie_has_genre :** Ce sont les tables intermédiaires qui permettent de faire la liaison entre la table movie et genre, director, actor. Comme cela nous avons l'ensemble des personnes / genres présentent dans tous les films.

L'ensemble de ces tables suffisent amplements à structurer et stocker les données dont nous avons besoin pour notre application. De plus nous devont interroger notre base de données lors des recherches spécifiques faites par l'utilisateur et ces tables répondent à notre demande.

*Note : Dans notre projet, vous pouvez retrouver la base de données (SQLite) qui est à l'emplacement : `cache/cache.db`. Nous avons décider de l'appelée ainsi, car elle fait vaiment office de cache, l'utilisateur peut la supprimer si elle serait corrompue, ou même par inadvertance et lors du lancement de l'application elle sera recrée automatiquement dans le dossier cache.*

---

## 2. Language

Pour ce projet, nous avons choisi d'utiliser Java. Voici les raisons qui nous ont poussé à le faire. Tout d'abord, nous avons déjà travaillé avec ce langage par le passé. Sachant cela, choisir une technologie qui ne nous est pas inconnue nous a permis de nous focaliser sur l'élaboration du projet en lui-même plutôt que de devoir faire de la veille technologique et de consommer le temps à disposition pour réaliser le projet afin de nous former avant de pouvoir entrer dans le vif du sujet.

En outre, Java est multi-plateforme se trouve déjà dans de nombreux systèmes. L'application résultante est donc beaucoup plus accessible, du point de vue de l'utilisateur, ce qui est un point non-négligeable.

Pour une application de ce type, il va sans dire qu'une interface graphique est indispensable. Créer des interfaces graphiques en Java est une tâche, certes, chronophage mais il est possible de concevoir des interfaces de très bonne facture. Java intègre désormais JavaFx, successeur de Swing. JavaFx permet notamment d'utiliser des feuilles de style CSS. Le fait de pouvoir allier nos connaissances de Java et du CSS nous a conforter dans l'idée de choisir ce langage pour réaliser ce projet.

---

### 3.  Bibliothèques externes

Nous avons choisi d'utiliser deux bibliothèques externes, voici lesquelles ont été choisi et pour quelle raison.

#### 3.1 SQLite

Sqlite est une bibliothèque écrite en C qui propose un moteur de base de données relationnelle accessible par le langage SQL.

Nous avons choisi d'intégrer cette bibliothèque car elle permet de ne pas reproduire un schéma habituel de client - serveur, mais d'être directement intégrée au programme où l'intégralité de la base de données est stockés dans un fichier indépendant de la plateforme. Ce qui permet d'une part de pouvoir la consulter et d'y faire des opération sans avoir besoin d'une connexion internet et d'une autre part de profiter de son fonctionnement multiplateforme.

De plus, SQLite est une bibliothèque open source et extrêmement légère. C'est pour cela que c'est le moteur de base de données le plus utilisé au monde, surtout dans des logiciels grand public ou des systèmes embarqués.

SQLite nous paraît donc très adapté pour ce genre de projet qui de plus ne demandera pas une gestion d'une immense quantité de films.

Liens vers la bibliothèque : [SQLite](https://bitbucket.org/xerial/sqlite-jdbc)

#### 3.2 JSON-java

JSON-java est une bibliothèque open source et populaire qui permet de décoder / encoder des fichiers JSON.

Nous avons choisi d'intégrer cette bibliothèque car elle est très légère et son utilisation est très simple.

Dans notre programme, nous devons décoder à plusieurs reprises des fichier JSON qui nous sont retournés par l'API, de ce fait nous avons pensé utile de l'utiliser.

Liens vers la bibliothèque : [JSON-java](https://github.com/stleary/JSON-java)

---

### 4. Fonctionnalités importantes

Nous allons vous expliquer le fonctionnement des fonctionnalités que nous pensons être les plus importantes et pertinentes.

Les informations porteront plus sur la logique en elle-même et non l'explication du code. Pour voir le code source, vous pouvez le retrouver à l'emplacement : `What2Watch/src/what2watch`.

#### 4.1 Recherche de fichiers

**Emplacement des classes :**
`What2Watch/src/FileFinder.java`
`What2Watch/src/FileBrowser.java`

Plusieurs API différentes comprennent des outils permettant de parcourir le contenu de dossiers de manière récursive. Dans le cadre de ce projet, c'est la réactivité et les performances qui ont guidé nos recherches.

Nous avons dans un premier temps choisi d'implémenter Java 8 Stream Parallel. Java 8 Stream Parallel a l'avantage d'être la méthode la plus performante en terme de temps nécessaire au parcours de dossiers comme le démontre ce benchmark:

| Test Name                | API      | Average Time |
| ------------------------ |:--------:| -----:|
| Java 8 Stream Parallel   | NIO      | 28.425S |
| Walk File Tree           | NIO      | 28.4718S |
| Java 8 Stream Sequential | NIO      | 29.62S |
| File Walker              | I/O      | 32.5256S |
| listFiles - Recursive    | I/O      | 32.5864S |
| listFiles - Queue        | I/O      | 33.7566S |
| commons-io - FileUtils   | I/O      | 1M5.413S |

 Lien vers le benchmark complet :  [io-recurse-tests](https://github.com/brettryan/io-recurse-tests#io-recurse-tests)

Néanmoins, nous avons par la suite décidé de "faire marche arrière" et d'utiliser une autre méthode. Walk File Tree. Nous avons pris cette décision car Walk File Tree est tout aussi performante que Java 8 Stream Parallel mais surtout car elle est plus flexible.

Elle permet de manipuler les fichier et les gérer les différents types de dossier analysés plus plus simplement. En outre, elle a le potentiele d'être plus facilement adaptable pour de futurs ajouts ou modifications.

Walk File Tree fonctionne sur le principe de la délégation. Il est nécessaire de créer une classe qui implémente l'interface "FileVisitor". Cette classe est chargée de réagir à différents évènements qui ont lieux lors du parcours de dossiers via les méthodes suivantes:

- **preVisitDirectory:** méthode appelée avant que le contenu d'un dossier ne soit parcouru.

- **postVisitDirectory:** méthode appelée lorsque tous les éléments contenus dans un dossier ont été parcouru.

- **visitFile:** méthode appelée lorsqu'un fichier est parcouru. C'est dans cette méthode que l'application appelle la logique permettant de sélectionner un fichier selon son type.

- **visitFileFailed:** méthode appelée lorsqu'il est impossible d'accéder à un fichier.

En conclusion, le prix à payer était faible comparé aux avantages apportés par l'usage de Walk File Tree d'où le choix de son intégration.

Plus d'informations sur Walk File Tree : [Walk File Tree](https://docs.oracle.com/javase/tutorial/essential/io/walk.html)

#### 4.2 Magic Numbers

**Branche dédiée à la fonctionnalité :** [feature-magic-numbers](https://github.com/raph-u/What2Watch/tree/feature-magic-numbers)

**Emplacement de la classe :** `What2Watch/src/FileFinder.java`

Dans un premier temps, l'application se contentait de ne traiter que les fichiers disposant d'une extension. Or, en se basant sur le dossier de films fictifs qui nous a été fournis pour nous aider dans le projet, nous avons réalisé qu'il était nécessaire de traiter les fichiers dépourvus d'extension également.

Afin de prendre en compte un maximum de fichiers différents, nous avons décider d'intégrer un système de reconnaissance de magic numbers (signatures de fichier).

Lors du parcours de dossier, l'application va bien évidemment récupérer tous les fichiers vidéos reconnus, c'est a dire, les fichiers disposant d'une extension que notre application prend en charge (avi,mkv,mpeg,wmv,m4v,mp4,flv,mov).

Lorsqu'un fichier dépourvu d'extension est rencontré, l'application lis les 4 premiers bytes du fichier constituant sa signature afin de la comparer avec un liste de signatures connues qui a été intégrée dans notre application.

Si la signature d'un fichier correspond à l'une des signatures de la liste, le fichier en question enferme alors du contenu vidéo et notre application le prendra dorénavant en charge.

La liste des signatures stockée dans l'application a été constituée d'après les signatures trouvées lors de tests mais également en puisant dans des bases de données de signatures telles que celles-ci : [garykessler.net](http://www.garykessler.net/library/file_sigs.html)

En terme de performances, nos tests on démontrés que l'intégration d'une telle fonctionnalité n'avait pas d'impact conséquent sur une machine équipé d'un SSD. En revanche, sur les machines à disque dur classiques, les délais d'attentes ajoutés lors de scans de dossier se ressentent réellement.

Il n'est pas envisageable de partir du principe que tous les utilisateurs ont un SSD. Nous avons préféré ne pas intégrer la fonctionnalité dans la version finale de notre application par soucis de performance. La fonctionnalité reste néanmoins disponible sur la branche qui lui est dédiée, à cette adresse : [feature-magic-numbers](https://github.com/raph-u/What2Watch/tree/feature-magic-numbers)


#### 4.3 Analyse des fichiers, récupération du titre du film

**Emplacement de la classe :** `What2Watch/src/ParsingFiles.java`

Pour que l'API puisse nous retourner les données du film que l'ont veut, il faut pouvoir lui passer le titre du film le plus épuré possible. Voici donc comment nous procédons:

Nous avons une première fonction qui va faire un remplacement par un espace de tout ce que l'on rentre à la main comme pattern, par exemple des termes comme : Xvid, bdrip, VOSTFR, etc. ainsi que des ponctuations : ".-\_()" (chaque "famille" de pattern sont séparées les unes des autres). Nous pouvons donc l'affiner au fur et à mesure.

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

**Emplacement de la classe :** `What2Watch/src/ApiHandler.java`

Concernant la récupération d'informations sur un film, nous utilisons l'API du site "www.themoviedb.org" : liens vers la documentation de l'API : [TMDB API](https://developers.themoviedb.org/3/getting-started).
Pour utiliser cette API nous devons utiliser une clef, nous avons donc stocké cette clef dans un fichier .env qui n'est pas à disposition dans le repository (question de sécurité durant la phase de développement).

Nous somme également limité au niveau du nombre de requête par seconde, ce qui implique une certaine attente supérieure à celle que nous aurions voulu au départ. Mais nous avons quand même décidé d'utiliser cette API car elle apporte tous ce que nous cherchons. Nous lui envoyons le titre du film (autant en Français qu'en Anglais) dont nous recherchons les informations, et l'API nous retourne tous ce dont nous avons besoin, en format JSON et dans la langue souhaitée. Il nous reste donc plus qu'à parser le tout et à l'afficher dans notre application.

#### 4.5 Interface utilisateur

![UI](screenshots/Movie informations.png "W2W - UI")

L'interface a initialement été pensée de manière à faire le lien entre ce que l'utilisateur voit lorsqu'il télécharge ses films, c'est à dire leur noms, et les les informations associées à ces films.

Mettre en avant dans l'interface une liste affichant les noms de films traités et exempts de caractères spéciaux nous a semblé être une bonne idée. Nous avons donc intégré cette liste dans l'application. Ce choix nous a notemment permis d'avoir une interface utilisable très rapidement et de faire plusieurs itérations afin d'obtenir une interface plaisante et fonctionnelle.

En outre, l'intégration d'une liste de noms a également l'avantage de pouvoir afficher plus de films en une fois que si nous avions décidé d'afficher des pochettes.

Afin de créer une interface utilisateur moderne, agréable et visuellement plaisante, nous avions pris en concidération l'intégration d'une bibliothèque incorporant le material design de google. Cette bibliothèque, nommée JFoeniX a été intégrée dans la branche UI-update. Néanmoins, la plus-value apportée par JFoeniX n'était pas suffisante, raison pour laquelle l'interface a été créer via les outils offerts par JavaFX.

[JFoeniX](http://www.jfoenix.com/)

##### Fenêtre de paramètres

![Settings](screenshots/Settings.png "W2W - Settings")

L'application dispose de deux fenêtres différentes. La première permet à l'utilisateur d'intéragir avec les films trouvés par l'application. La seconde, quant à elle, permet à l'utilisateur de définir dans quel dossier il souhaite que l'application récupère des films.

Dans le cas où l'application est lancée pour la première fois, c'est la fenêtre des paramètres qui est présentée à l'utilisateur en premier lieu afin de lui permettre de choisir un dossier de films avant d'utiliser l'application. Si ce dossier est supprimé ou déplacé entre deux sessions d'utilisation de l'application, la fenêtre des paramètres est à nouveau présentée à l'utilisateur au prochain lancement.

##### Fenêtre principale

A l'image d'un bon nombre d'application actuelles, lorsque l'utilisateur lance l'application, la vue principale présente des intructions qui lui permettent de se familiariser avec l'interface en lui expliquant les fonctionnalités offertes par les contrôles pricipaux. Ces instructions facilitent le premier contact entre l'utilisateur et l'application, lui évitant ainsi une mauvaise expérience.

![Home](screenshots/Home.png "W2W - Home")

Du point de vue des contrôles voici ce que nous avon décidé d'intégrer dans la vue principale:

- **Bouton de paramètres:** ce bouton donne accès à la fenêtre des paramètres

- **Bouton de rafraichissement:** permet à l'utilisateur de scaner un dossier pour récupérer la liste de films qui s'y trouvent ou mettre à jour une liste existante

- **Combobox:** permet de sélectionner un critère de recherche de film. La sélection d'un critère met à jour l'interface. Ex. lorsque l'utilisateur choisi de faire une recherche par année, l'application affiche deux nouveaux champs de texts dédiés à l'insertion d'une plage d'année de début et de fin

- **Textfields:** tous les textfields de la vue principales sont "sensibilisés" à l'evenement "key released". En substance, lorsque l'utilisateur tape des termes de recherche, la liste de film se met à jour en temps réel et au fur et à mesur qu'il tape au clavier.

- **Poster de film:** lorsque l'on click sur le poster d'un film, ce dernier passe en mode plein-écran

- **Bouton play:** lance la lecture de film à l'aide du lecteur disponible sur l'OS de l'utilisateur

Exemple de recherche par plage d'années:

![Search](screenshots/Search.png "W2W - Search")


#### 4.6 Utilisation "offline" de l'application

Un point que nous trouvons très intéressant et important dans notre application, est le fait qu'elle soit utilisable sans avoir de connexion internet.

Avoir choisi d'utiliser une base de données en SQLite nous permet de pouvoir la consulter à n'importe quel moment, sans être dépendant d'une connexion internet. De ce fait, du moment que l'utilisateur à lancé au moins une fois la recherche d'informations en ayant internet, il est possible de récupérer toutes les informations de ces films autant de fois qu'il le veut, sans avoir internet. Par contre il sera donc impossible de récupérer les informations de nouveaux films ajoutés dans son dossier sans avoir internet.

Nous avons donc mis en place toute une gestion de la connexion à internet. Nous avons une méthode qui permet d'ouvrir un socket vers le site "www.google.com" et qui nous retourne true ou false, respectivement: internet accessible ou non. Nous regardons donc à chaque début de scan si l'utilisateur à accès à internet ou non. Dans le cas ou il ne l'a pas, nous affichons on message d'alert. Notre application gère aussi le cas ou il serait possible que l'utilisateur perde la connexion au milieu d'une recherche. Si cela arrive, toutes les données non-trouvées seront égale à "Unknown" et l'application affiche un message d'alert.

Nous avons de plus intégré en bas à droite de l'application, un voyant vert ou rouge qui permet d'indiquer à l'utilisateur si il a une connexion internet ou pas. Au niveau du code, c'est un thread lancé au lancement de l'application et qui vérifie chaque seconde si internet est accessible ou pas. Ce thread se ferme correctement lorsque l'application se ferme.

---

### 5. Installation de What2Watch

Voici toutes les étapes à suivre pour installer notre application sur votre ordinateur. Que vous soyez sur Windows, Mac OS ou Linux il n'y aura aucun problème, car l'application est entièrement multiplateforme.


#### 5.1 JRE

Pour pouvoir exécuter des programmes Java (.jar) vous êtes obligé d'installer le JRE (Java SE Runtime Environment). Il vous faut obligatoirement le JRE 8u111.

*Note : Si vous avez déjà Java 8 d'installer vu pouvez sauter cette étape.*

Pour voir la version de java installer :

- **Sous Windows :** Aller dans le panneau de configuration -> Programmes -> Programmes et fonctionnalités. Puis rechercher Java et vous allez voir la version installée.

- **Sous Unix :** Ouvrez un terminal puis tapper la commande : `java -version`. Si vous avez un message d'erreur c'est que java n'est pas installé, sinon il faut que vous ayez un message : "Java version 1.8.x".

Téléchargement du JRE :

Voici le lien de téléchargement : [JRE 8u111](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

Il suffit de télécharger et installer le JRE 8u111 prévu pour l'OS de votre choix.

Pour l'installation en ligne de commande via Linux Ubuntu et Linux Debian, voici deux très bon tutoriels (c'est pour installer le JDK 8, qui intègre lui-même le JRE 8):
- [JDK8 - Ubuntu](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)
- [JDK8 - Debian](http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html)

#### 5.2 Application What2Watch

Pour installer notre application sur votre ordinateur, que vous ayez Windows, Mac OS ou Linux, c'est le même procédé. Il faut télécharger l'archive "What2Watch-deployment.zip" qui se trouve sur [GitHub](https://github.com/raph-u/What2Watch/tree/deployment). Pour cela cliquez simplement sur le bouton "Clone or download" et choisir "Download ZIP".

Une fois l'archive extraite, vous trouverez à l'intérieur un dossier "What2Watch" vous pouvez le placer ou vous le voulez (par exemple dans votre dossier de programmes).

À l'intérieur du dossier, vous trouverez un fichier nommer "What2Watch.jar". Ce fichier est le fichier à exécuter pour lancer l'application.

L'ensemble des fichiers / dossiers présent dans le dossier What2Watch doivent y rester, si vous voulez avoir le fichier What2Watch.jar par exemple sur votre bureau, il suffit d'en créer un raccourci, puis vous pouvez placer ce raccourci ou bon vous semble.

#### 5.3 Lancement de l'application

Si vous avez une interface graphique :
- Ouvrez le fichier "What2Watch.jar" en double cliquant dessus. (Si le fichier s'ouvre pas, veuillez l'ouvrir avec le programme : "Java(TM) Platform SE binary").

Si vous utilisez un terminal :
- Lancer votre terminal préféré et tapper la commande : `java -jar <nom_du_fichier.jar>`

---

### 6. Reprise du projet

Nous avons découpé la reprise du projet en plusieurs étapes, pour pouvoir les expliquer comme il faut. Il suffit de suivre les points un par un et vous pourrez reprendre le projet sans aucun problème.

#### 6.1 GitHub

L'ensemble du projet est disponible sur GitHub : [What2Watch](https://github.com/raph-u/What2Watch)

Vous pouvez donc récupérer l'ensemble du repository et par exemple l'ouvrir avec un outil de versionning (nous avons utilisé GitKraken).

Ou récupérer la branche de votre choix, pour ensuite en modifier le contenu.

Voici comment notre système de branches fonctionne :

- **Master :** Cette branche contient uniquement des versions livrables du projet.

- **Develop :** Cette branche contient des versions stables du projet, mais toujours en cours de développement.

- **Deployment :** Cette branche contient uniquement un dossier délivrable pour le client. Il contient donc le dossier lib et le fichier What2Watch.jar ("exécutable" de l'application). Ce dossier peut être généré automatiquement avec l'IDE Netbeans que nous utilisons. Avec cette branche nous pouvons donc télécharger facilement la version stable de notre choix que nous voulons lancer sur notre ordinateur.

- **Toutes les autres branches :** Pour chaque grande fonctionnalité (généralement plus de un ou deux commits) nous avons créé une branche spécifique à celle-ci et travaillions dessus jusqu'à avoir une version stable et ensuite faire un merge sur la branche develop.

#### 6.2 JDK

Nous avons utilisé le JDK 8u111 pour complilé notre application. Il est donc impératif de prendre la même version. Voici le lien de téléchargement : [JDK 8u111](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Il suffit de télécharger et installer le JDK 8u111 prévu pour l'OS de votre choix.

Pour l'installation en ligne de commande via Linux Ubuntu et Linux Debian, voici deux très bon tutoriels :
- [Ubuntu](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)
- [Debian](http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html)

#### 6.3 IDE

Vous pouvez utiliser n'importe quel IDE qui permet de reprendre un projet Java, mais nous vous recommandons tout de même d'utiliser le même que nous qui est Netbeans (version 8.2 ).

Liens de téléchargement : [Netbeans](https://netbeans.org/downloads/)

*Note : Ne pas oublié d'indiquer le chemin vers le JDK installé au point "6.2 JDK" lors de l'installation*

Il ne vous reste donc plus qu'à ouvrir le projet Java de la branche que vous voulez (téléchargé au préalable, voir point "6.1 GitHub"). Exemple : `What2Watch-develop/What2Watch`

Si vous utilisez Netbeans, il n'y a rien d'autre à faire. Toutes les bibliothèques sont déjà liées au projet etc. Si vous utilisez un autre IDE, il se peut qu'il faudra lier les bibliothèques au projet. Vous pouvez les retrouver dans le dossier `What2Watch/libraries`.

#### 6.4 API Key

Notre application utilise une clef d'API et pour ne pas qu'elle se retrouve publiquement sur le repository, nous avons décider de la stocker dans un fichier ".env" lui-même stocké sur notre ordinateur (le fichier .env est ignoré lors des commits grâce au fichier .gitignore).

D'abbord, vous devez récupérer votre propre clef d'API sur le site [The Movie DB](https://www.themoviedb.org/) car nous ne fournissons pas la clef d'API avec notre application. Pour cela il suffit de s'incrire sur le site et de demander une API KEY, c'est entièrement gratuit et instantané.

Ensuite, il faut que vous créiez un fichier ".env" dans le dossier du projet What2Watch (à la même hauteur que le dossier "src"). À l'intérieur de ce fichier, il faut écrire cette ligne : `api_key:xxxxxxxxxxxxxxxxx` en remplaçant les "x" par votre clef.


**Important:**

Pour finir, il faut que vous sachiez encore une chose, aller dans le code source de la classe "ApiHandler". Il y a un attribut de classe qui s'appelle "apiKey", sa valeur est égale à "getApiKey()". C'est une méthode de classe qui va permettre d'aller récupérer le contenu du fichier ".env" et donc de récupérer la valeur de la clef d'API. **Si vous déployez votre application, n'oubliez pas de changer la valeur de l'attribut "apiKey" par la valeur de la clef d'API et non par la méthode, car le fichier ".env" ne sera pas disponible chez l'utilisateur final.**

---

### 7. Bogues restants

- **Blocage de l'application sur fond blanc:** Durant nos tests, nous avons découvert que lors de son lancement, l'application peut rester figée sur un fond blanc. Nous avons rencontré ce bogue sur une machine en particulier. En outre, nous n'avons pas été en mesure de le reproduire, raison pour laquelle il n'a pas encore pu être résolu. Dans le cas où vous rencontrez ce bogue, nous vous invitons à lancer l'application sur une autre machine ou un autre OS.

- **Bug graphique:** Lorsque la liste de films est suffisamment longue et qu'elle comporte un film dont le titre est plus long que le slider horizontal, un carré blanc apparaît dans l'extrémité inférieure droite.

---

### 8. Améliorations possibles

- **Bouton d'interruption de scan:** Dans l'état actuel de l'application, l'utilisateur ne peut pas interrompre un scan lorsqu'il a été lancé. Pour contrer ce problème, un bouton d'interruption peut être intégrer dans l'application.

- **Image explicative de la partie droite de l'application:** Il serait bien que l'image s'adapte à la tâche en cours d'exécution. Par ex. afficher le tutoriel lors du lancement, afficher une image expliquant que l'application est en train de scaner un dossier, etc.

- **Multiplicité des critères de recherche:** L'application limite les recherches à une seule catégorie à la fois. Il serait néanmoins possible de supporter les recherches à critères multiples.

- **Ajout de films dans la base de données sans connexion internet:** Lorsque l'on perd la connexion internet lors d'un scan, les champs des films qui n'ont pas encore été traités vont être définis à "Unknown". Il serait plus adapté de ne pas traiter les films lorsque la connexion est interrompue.

- **Gestion des films à base commune (JSON):** Lorsque l'application traite le JSON récupéré via l'API, c'est la première occurance de film qui est insérée dans la base de donnée. Par ex. pour la récupération du film "Robocop", les informations récupérées via le JSON sont celles du film réalisé en 1987 ainsi que celles du reboot sortis en 2014. L'application ne gère pas les différentes occurences possibles et récupère uniquement les informations du film récupéré en premier à la place. Il serait envisageable de ne pas traiter les films dont on ne peut pas récupérer les informations avec certitude.

---

### 9. Conclusion

**Communication**

Durant l'élaboration du projet, nous avons énormément communiqué afin de comparer nos points de vue et intégrer la solution la plus adéquate. Nous avons donc appris que la communication est un point essentiel pour que le projet se développe en évitant un maximum de problèmes.

**Outils de versionning**

Ce projet nous a permis d'apprendre à utiliser un outil de versionning. Nous avons choisi GitKraken afin de gérer l'évolution de notre application. N'ayant jamais travaillé avec git par le passé, ce projet a été un excellent moyen de nous former avec cette technologie et de nous faire comprendre l'avantage qu'elle apporte dans la réalisation d'un projet.
