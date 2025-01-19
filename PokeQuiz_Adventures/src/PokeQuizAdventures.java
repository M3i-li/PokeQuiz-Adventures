import extensions.File;
import extensions.CSVFile;
import java.lang.reflect.InaccessibleObjectException;

class PokeQuizAdventures extends Program{

    // chemin d'accès au divers dossier
    final String CHEMIN_MONDE = "./ressources/monde/"; //chemin relatif à partir du niveau d'arborescence du compile.sh et du run.sh !!
    final String CHEMIN_QUESTIONS = "./ressources/questions/";
    final String CHEMIN_POKEMON_CSV = "./ressources/pokemon/CSV/";
    final String CHEMIN_POKEMON_ASCII = "./ressources/pokemon/ASCII/";
    final String CHEMIN_SAUVEGARDE = "./ressources/Saves/";


    final String[][] QUESTION_FACILE = recupererQuestion("questions_facile");
    final String[][] QUESTION_MOYENNE = recupererQuestion("questions_moyenne");
    final String[][] QUESTION_DIFFICILE = recupererQuestion("questions_difficile");

    int compteur_car = 0; // permet de savoir combien de caracères effacer dans chaque dialogue
    

    // Codes ANSI pour les couleurs
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD =  "\u001B[1m";
    final String[][] COULEUR = new String[][] {{"RED",ANSI_RED},{"YELLOW",ANSI_YELLOW},{"GREEN",ANSI_GREEN},{"BLUE",ANSI_BLUE},{"CYAN",ANSI_CYAN},{"PURPLE",ANSI_PURPLE},{"NULL",""},{"BOLD",ANSI_BOLD}};


    //Pokémons

    final String[][] STARTERSCSV = recupererPokemonCSV("starters");
    final Pokemon[] STARTERS = recupererPokemon (STARTERSCSV) ;
    final String[][] ADVERSAIRESCSV = recupererPokemonCSV("Adversaires");
    final Pokemon[] ADVERSAIRES = recupererAdversaires(ADVERSAIRESCSV);
    final String[][] BOSSCSV = recupererPokemonCSV("Boss");
    final Pokemon[] BOSS = recupererAdversaires(BOSSCSV);

    // Mondes 

    Monde monde1 = newMonde ("Bourg Palette", new Pokemon[] {getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Roucoul"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Rattata"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Chenipan"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Coconfort"), getPokemon(BOSSCSV,BOSS,"Ronflex")}); 
    Monde monde2 = newMonde ("Argenta", new Pokemon[] {getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Osselait"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Psykokwak"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Triopiqueur"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Galopa"), getPokemon(BOSSCSV,BOSS,"Ectoplasma")}); 
    Monde monde3 = newMonde ("Jadielle", new Pokemon[] {getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Porygon"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Nosferapti"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Spectrum"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Voltali"), getPokemon(BOSSCSV,BOSS,"Mewtwo")}); 
    Monde monde4 = newMonde ("Irisia", new Pokemon[] {getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Evoli"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Feunard"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Noctali"), getPokemon(ADVERSAIRESCSV,ADVERSAIRES,"Luxray"), getPokemon(BOSSCSV,BOSS,"Mew")}); 
    // idée de continent pour changer de génération !! (Johto ( passage a la gen 2))
    // nom des mondes : Bourg Geon, Ecorcia, Irisia
    // Le reste si on a la temps mdr

    Monde [] continent = new Monde[]{monde1, monde2, monde3, monde4};

    //--------------------------------------------------------CHARGEMENT DES CSV POKEMONS---------------------------------------------------------------

    // fonction qui sert à récupérer les pokémons dans le même format que dans le format csv 
    String[][] recupererPokemonCSV(String nomFichier){
        CSVFile fichier = loadCSV(CHEMIN_POKEMON_CSV + nomFichier + ".csv", ';');
        String[][] pokedex = new String[rowCount(fichier)-1][columnCount(fichier)+1];
        for(int pokemon = 0; pokemon < length(pokedex,1); pokemon++){
            for(int caracteristiques = 0; caracteristiques < length(pokedex,2); caracteristiques++){
                pokedex[pokemon][caracteristiques] = getCell(fichier, pokemon+1, caracteristiques);
            }
        }
        return pokedex;
    }

    // fonction qui sert à récupérer les pokémons en tant que types Pokemons dans un tableau simple
    Pokemon[] recupererPokemon (String[][] pokedexCSV) {
        Pokemon[] pokedexPokemonJoues = new Pokemon[length(pokedexCSV,1)];
        for ( int i = 0; i<length(pokedexCSV); i++) {
            String[] attaques = new String[] {pokedexCSV[i][4],pokedexCSV[i][5],pokedexCSV[i][6]};
            int[] puissanceAttaques = new int[] {stringToInt(pokedexCSV[i][7]),stringToInt(pokedexCSV[i][8]),stringToInt(pokedexCSV[i][9])}; //on a besoin de nombres pour les dégâts donc utilisation de stringToInt()
            pokedexPokemonJoues[i] = newPokemon(pokedexCSV[i][0],pokedexCSV[i][1],stringToInt(pokedexCSV[i][2]),stringToInt(pokedexCSV[i][3]),attaques, puissanceAttaques, getEvolution(pokedexCSV,pokedexPokemonJoues, i));
        }
        return pokedexPokemonJoues; //tableau de l'ensemble des pokémons qui sont maintenant de type Pokemon
    }

    // fonction qui sert à récupérer les adversaires en tant que types Pokemon dans un tableau simple
    Pokemon[] recupererAdversaires (String[][] pokedexCSV) {
        Pokemon[] pokedexAdversaires = new Pokemon[length(pokedexCSV,1)];
        for ( int i = 0; i<length(pokedexCSV); i++) {
            String[] attaques = new String[] {pokedexCSV[i][3],pokedexCSV[i][4],pokedexCSV[i][5]};
            int[] puissanceAttaques = new int[] {stringToInt(pokedexCSV[i][6]),stringToInt(pokedexCSV[i][7]),stringToInt(pokedexCSV[i][8])};
            pokedexAdversaires[i] = newAdversaire(pokedexCSV[i][0],pokedexCSV[i][1],stringToInt(pokedexCSV[i][2]),attaques, puissanceAttaques);
        }
        return pokedexAdversaires; //tableau de l'ensemble des pokémons qui sont maintenant de type Pokemon
    }

    Pokemon getPokemon (String[][] pokedexCSV, Pokemon[] pokedex, String nom) {
        //retourne le pokemon voulu se trouvant dans le pokédex en fonction de son nom (!! sensible à la casse, ne pas oublier les majuscules)
        int indicePokemon=0;
        boolean trouve = false;
        int i = 0;
        while (i<length(pokedexCSV) && !trouve){
            if (equals(pokedexCSV[i][0],nom)) { //on utilise de tableau contenant des CSV car celui des Pokemon contient des ANSI COLOR dans le code 
                indicePokemon = i;
                trouve = true;
            } else {
                i ++;
            }
        }
        return pokedex[indicePokemon]; //on renvoie le pokémon de type pokémon (il est à la même place dans les deux tableaux)
    }

    Pokemon getEvolution(String[][] pokedexCSV, Pokemon[] pokedex, int indicePokemon) { 
        //récupère l'évolution du pokémon en paramètre si elle existe
        int evolutionIndiceRelatif = stringToInt(pokedexCSV[indicePokemon][10]); //on récupère la valeur correspondant à l'évolution dans le tableau : -1: n'a pas d'évolution, valeur >0: nombre de sauts vers l'évolution du pokémon en fonction de sa position dans le tableau
        if (evolutionIndiceRelatif == 0) {
            return null;
        } else {
            return pokedex[indicePokemon+evolutionIndiceRelatif]; //va chercher dans le tableau qui sert de pokedex le pokémon voulu
        }
    }

    //------------------------------------------------------------CREATION DE TYPES------------------------------------------------------------------

    
    Pokemon newPokemon (String nom, String couleurNom, int vieMax, int experienceMax, String[] attaques, int[] puissanceAttaques, Pokemon evolution){ //à rajouter par la suite: boolean evolution, String type 
        //instancie un nouveau pokémon
        Pokemon p = new Pokemon();
        p.nom = nom;
        p.couleurNom = new String[]{couleurNom};
        p.experience = 0;
        p.experienceMax = experienceMax;
        p.vieMax = vieMax;
        p.vie = p.vieMax;
        p.attaques = attaques;
        p.puissanceAttaques = puissanceAttaques;
        p.evolution = evolution;
        return p;
    } //Fonctionne, il faut juste se positionner dans src lors de l'éxecution de ijava !!

    void testNewPokemon () {
        Pokemon pokemonTest = newPokemon ("Pokémon test", "BLUE", 250, 100, new String[]{"Éclair","Flammèche","Pic-Pic"}, new int[]{5,10,20}, null);
        assertEquals ("Pokémon test", pokemonTest.nom);
        assertEquals ("NULL", pokemonTest.couleurNom [0]);
        assertEquals (0, pokemonTest.experience);
        assertEquals (250, pokemonTest.vie);
        assertEquals (250, pokemonTest.vieMax);
        assertEquals (100, pokemonTest.experienceMax);
        assertEquals (0, pokemonTest.experience);
        assertEquals (100, pokemonTest.experienceMax);

        assertEquals ("Éclair", pokemonTest.attaques [0]);
        assertEquals ("Flammèche", pokemonTest.attaques [1]);
        assertEquals ("Pic-Pic", pokemonTest.attaques [2]);

        assertEquals (5, pokemonTest.puissanceAttaques [0]);
        assertEquals (10, pokemonTest.puissanceAttaques [1]);
        assertEquals (20, pokemonTest.puissanceAttaques [2]);
    }

    Pokemon newAdversaire (String nom, String couleurNom, int vieMax, String[] attaques, int[] puissanceAttaques){
        Pokemon p = new Pokemon();
        p.nom = nom;
        p.couleurNom = new String[] {"BOLD", couleurNom};
        p.vieMax = vieMax;
        p.vie = p.vieMax;
        p.experience = -1;
        p.experienceMax = -1;
        p.attaques = attaques;
        p.puissanceAttaques = puissanceAttaques;
        return p;
    }

    void testNewAdversaire () {
        Pokemon ennemiTest = newAdversaire("Coconfort", "NULL", 60, new String[]{"Armure","Armure","Armure"}, new int[]{5,10,20});
        assertEquals ("Coconfort", ennemiTest.nom);
        assertEquals ("BOLD", ennemiTest.couleurNom [0]);
        assertEquals ("NULL", ennemiTest.couleurNom [1]);
        assertEquals (60, ennemiTest.vie);
        assertEquals (60, ennemiTest.vieMax);

        assertEquals ("Armure", ennemiTest.attaques [0]);
        assertEquals ("Armure", ennemiTest.attaques [1]);
        assertEquals ("Armure", ennemiTest.attaques [2]);

        assertEquals (5, ennemiTest.puissanceAttaques [0]);
        assertEquals (10, ennemiTest.puissanceAttaques [1]);
        assertEquals (20, ennemiTest.puissanceAttaques [2]);
    }

    Monde newMonde (String nom, Pokemon[] ennemis) {
        Monde m = new Monde();
        m.nom = nom;
        m.ennemis = ennemis;
        //m.type = type;
        //m.level = difficulte;
        return m;
    }

    void testNewMonde () {
        Pokemon ennemiTest = newAdversaire ("ennemi test", "NULL", 60, new String[]{"Éclair","Flammèche","Pic-Pic"}, new int[]{5,10,20});
        Monde MondeTest = newMonde ("Monde test", new Pokemon[]{ennemiTest});
        assertEquals (ennemiTest, MondeTest.ennemis[0]);
    }

    //--------------------------------------------------FONCTIONS D'AFFICHAGE--------------------------------------------------------

    
    void printTmp (String chaine) { 
    // affiche chaque caractère d'une chaine entrée en paramètre selon un délai de 80 ms
    
        for (int c = 0; c<length(chaine); c++) {
            print(charAt(chaine, c));
            delay (20);
            compteur_car++; //on incrémente le nombre de caractères à devoir effacer par la suite
        }
        delay(500);

    }

    void printlnTmp (String chaine) { //affiche en passant à la ligne les caractères d'une ligne de texte dans la console 
        printTmp(chaine);
        println();
        compteur_car=0;//pas encore trouvé de moyen d'effacer un texte qui a été passé à la ligne, donc je reset le compteur de caractère pour que la fontion EffacerTmp ne la prenne pas en compte
    }

    void effaceTmp (int compteur) { 
        // supprime chaque carcatère d'une ligne écrite dans le terminal selon un délai de 50 ms
        for (int c = compteur; c>=0; c=c-1) {
            print("\b \b");
            delay(50);
        }
        compteur_car = 0; //réinitialisation du compteur de caractères
    }

    void afficheAttaques (Pokemon pokemonJoue) { // affiche les attaques disponibles du Pokémon
        printlnTmp("Choisissez une attaque");
        for (int i = 0; i < length(pokemonJoue.attaques); i++){
            print( i + 1 + " : " + pokemonJoue.attaques[i] + " : " + pokemonJoue.puissanceAttaques[i] + " dégâts !");
            if(i == 0){
                println("  (Question facile)");
            }
            if(i == 1){
                println("  (Question moyenne)");
            }
            if(i == 2){
                println("  (Question difficile)");
            }
            delay(200);
        }
    }

    String correspondanceANSI (String couleur) {
        //renvoie l'ANSI correspondant dans le tableau de correspondance (variable globale) en fonction de son identifiant couleur
        for (int i=0; i<length(COULEUR,1); i++) { //parcours du tableau à deux dimension COULEUR qui contient un nom texte qui sert d'identifiant de la couleur, et la variable ANSI color correspondante (variable globale)
            if (equals(couleur, COULEUR [i][0])) {  // si l'identifiant match avec le texte entré en paramètre
                return COULEUR[i][1]; //on renvoie l'ANSI correspondant
            }
        }
        return ""; //cas normalement impossible: il ne trouve pas la couleur dans le tableau et donc ne renvoie rien (ca évitera le renvoi d'une erreur et au pire le pokémon sera écrit en blanc)
    }

    String afficheNomPokemon (Pokemon pokemon) {
        // affiche le nom du pkémon selon les caractéristiques ANSI qu'il possède
        String affichageNom = "";
        for (int i=0; i<length(pokemon.couleurNom); i++) {
            affichageNom = affichageNom + correspondanceANSI (pokemon.couleurNom[i]); //ajout de toutes les caractéristiqus ANSI du pokémon (ex: les boss sont en BOLD et en COULEUR)
        }
        return affichageNom + pokemon.nom + ANSI_RESET + ANSI_WHITE;
    }

    void afficheAsciiPokemon (Pokemon pokemon) {
        String couleurANSI= "";
        for (int i = 0; i<length(pokemon.couleurNom); i++) {
            if (!equals(pokemon.couleurNom[i],"BOLD")){
                couleurANSI = couleurANSI + correspondanceANSI (pokemon.couleurNom[i]);
            }
        }
        print(couleurANSI);
        chargerFichier(CHEMIN_POKEMON_ASCII+pokemon.nom+".txt");
        print (ANSI_RESET + ANSI_WHITE);
    }

    String afficherVie (Pokemon pokemon) {
        int vie = pokemon.vie;
        if (vie <= 20) {
            return ""+ANSI_RED+vie+ANSI_RESET+ANSI_WHITE;
        } else {
            return ""+vie;
        }
    }
    //--------------------------------------------FONCTIONS DE GESTION DE FICHIERS EXTERNES---------------------------------------------------

    //fonction qui charge des ressources pour l'affichage du monde
    String chargerRessourceMonde(String cheminFichier){
        extensions.File fichier = newFile(CHEMIN_MONDE + cheminFichier);
        String contenue = "";
        while(ready(fichier)){
            contenue = contenue + readLine(fichier) + "\n";
        }
        return contenue;
    }

    void chargerFichier (String cheminFichier) {
        extensions.File fichier = newFile (cheminFichier);
        while (ready(fichier)) {
            println(readLine(fichier));
        }
    }

    String[][] chargerCSVTotal(String nomJoueur) {
        CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDE + nomJoueur + ".csv", ';');
        String[][] infosJoueur = new String[rowCount(fichier)][columnCount(fichier)];
        for(int infos = 0; infos < length(infosJoueur,2); infos++){
            infosJoueur[0][infos] = getCell(fichier, 0, infos);
        }
        return infosJoueur;
    }

    String chargerCSV(String nomJoueur, int indice) {
        CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDE + nomJoueur + ".csv", ';');
        return getCell(fichier, 0, indice );
    }

    // fonction qui vas charger des ressources pour l'affichage dans un tableau
    String[] chargerRessourcesTableau(String cheminFichier){
        int nbLignes = nbLignesFichier(cheminFichier);
        String[] contenue = new String[nbLignes];
        extensions.File fichier = newFile(cheminFichier);
        int ligne = 0;
        while(ready(fichier)){
            contenue[ligne] = readLine(fichier);
            ligne++;
        }
        return contenue;
    }

    // fonction qui retourne le nombre de ligne d'un fichier .txt
    int nbLignesFichier(String cheminFichier){
        extensions.File fichier = newFile(cheminFichier);
        int nbLignes = 0;
        while(ready(fichier)){
            readLine(fichier);
            nbLignes++;
        }
        return nbLignes;
    }

    void creationSauvegarde (String nom) { // crée la sauvegarde initiale d'un joueur
        //newFile ("./ressources/Saves/"+nom+".CSV");
        String[][] donneesCSV = new String [1][8];
        donneesCSV[0][0] = nom; //nom du Joueur
        donneesCSV[0][1] = "NULL"; // nom de son pokémon
        donneesCSV[0][2] = "NULL"; //vie du pokémon
        donneesCSV[0][3] = "NULL"; // experience du pokémon
        donneesCSV[0][4] = "-1"; // niveau du monde
        donneesCSV[0][5] = "0"; //numéro de l'ennemi du monde
        donneesCSV[0][6] = "NULL"; //vie de l'ennemi
        donneesCSV[0][7] = "0"; //nombre de tours du combat
        saveCSV(donneesCSV, "./ressources/Saves/"+nom+".csv",';');
    }

    boolean fichierCSVExiste (String nomJoueur) {
        String[] allSaveFiles = getAllFilesFromDirectory ("./ressources/Saves/");
        for (int i=0; i<length(allSaveFiles); i++) {
            if (length(allSaveFiles[i])-4 == length(nomJoueur)) {
                if(equals(substring(allSaveFiles[i],0,length(allSaveFiles[i])-4),nomJoueur)) {
                    return true;
                }
            }
        }
        return false;
    }

    void modifierCSV(String nomJoueur,int indice, String valeur) {
        String[][] fichierCSV = chargerCSVTotal(nomJoueur);
        fichierCSV[0][indice] = valeur;
        saveCSV(fichierCSV, "./ressources/Saves/"+nomJoueur+".csv",';');
    }

    void sauvegardeCombat (String nomJoueur, Pokemon pokemonJoue, Pokemon ennemi,int nombreTour) {
        modifierCSV (nomJoueur,1,pokemonJoue.nom);
        modifierCSV (nomJoueur,2,""+pokemonJoue.vie);
        modifierCSV (nomJoueur,3,""+pokemonJoue.experience);
        modifierCSV (nomJoueur,6,""+ennemi.vie);
        modifierCSV (nomJoueur,7,""+nombreTour);
    }

    Pokemon chargerPokemon (String nomJoueur) {
        Pokemon pokemon;
        pokemon = getPokemon(STARTERSCSV,STARTERS,chargerCSV(nomJoueur,1));
        pokemon.vie = stringToInt(chargerCSV(nomJoueur,2));
        if (pokemon.vie == 0) {
            pokemon.ko = true;
        }
        pokemon.experience = stringToInt(chargerCSV(nomJoueur,3));
        return pokemon;
    }


    //--------------------------------------------FONCTIONS DE CONTROLE DE CHOIX---------------------------------------------------


    boolean choixPasCorrecteIntervalle (String saisie, char choixDeb, char choixFin) {
        //vérifie si un choix est vide ou n'est pas compris dans un interalle exclu de nombres donnés (pratique quand ya une liste de choix numérotés)
        if (length(saisie) != 1) {
            return true;
        } else if ((charAt(saisie,0)<choixDeb || charAt(saisie,0)>choixFin)) {
            return true;
        } else {
            return false;
        }
    }

    void testChoixPasCorrectIntervalle () {
        assertTrue (choixPasCorrecteIntervalle("", '1','8')); //saisie vide
        assertTrue (choixPasCorrecteIntervalle("7",'1','3')); //saisies hors interval
        assertTrue (choixPasCorrecteIntervalle("-6",'1','3'));
        assertTrue (choixPasCorrecteIntervalle("0",'1','3'));
        assertFalse (choixPasCorrecteIntervalle("2",'0','4')); //saisies dans l'interval 
        assertFalse (choixPasCorrecteIntervalle("1",'1','2'));
    }

    int choixIntervalle(char choixDeb, char choixFin, String phrase){
        print("Entrez votre choix: ");
        String saisie = readString();
        while (choixPasCorrecteIntervalle(saisie, choixDeb, choixFin)) {
            println(phrase);
            print("Entrez votre choix: ");
            saisie = readString();
            println();
            delay(100);
        }
        return stringToInt(saisie);
    }

    boolean pikapikachuChoix(String saisie) {
        return (equals(saisie,"uwu") || equals(saisie,"UWU") || equals(saisie,"Uwu") || equals(saisie,"UWu") || equals(saisie,"uWu") || equals(saisie,"uwU"));
    }

    boolean caractereImpossibleFichier(String chaine, char[] carBannis) {
        for (int c = 0; c<length(chaine); c ++) {
            for (int i=0; i<length(carBannis); i++) {
                if  (charAt(chaine,c) == carBannis[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    Pokemon choixStarter (Pokemon poke1, Pokemon poke2, Pokemon poke3, Pokemon poke4) {
        //permet au joueur de choisir son starter pokémon
        int num;
        String saisie;
        print("Entrez votre choix: ");
        saisie = readString();
        while (choixPasCorrecteIntervalle(saisie, '1', '3') && !pikapikachuChoix(saisie)) {
            println("Choix incorrect. Veuillez entrer un choix de starter entre 1 et 3");
            print("Entrez votre choix: ");
            saisie = readString();
            println();
            delay(100);
        }
        if (pikapikachuChoix(saisie)) {
            num = 4;
        } else {
            num = stringToInt(saisie);
        }
        
        if (num==1) {
            return poke1;
        } else if (num==2) {
            return poke2;
        } else if (num == 3){
            return poke3;
        } else { // num vaut 4 et il s'agit de l'easter egg
            printTmp (ANSI_ITALIC+"Easter Egg !! ");
            printlnTmp ("Vous avez débloqué Pichuwu :3 !!"+ANSI_RESET + ANSI_WHITE);
            delay(500);
            println();
            return poke4;
        }
    }

    int choixMenu() {
        //permet au joueur de choisir dans le menu
        int num;
        String saisie;

        clearScreen();
        println(chargerRessourceMonde("Logo PokeQuiz.txt"));
        println();

        println(chargerRessourceMonde("Menu.txt"));
        println();

        return choixIntervalle('1', '3', "Veuillez entrer un choix entre 1 et 3");
    }

    String choixPrenom () {
        //structure de controle d'une saisie correcte du nom
        String prenom;
        print("Ton nom: ");
        prenom = readString();
        while (length(prenom)>50 || length(prenom)==0 || caractereImpossibleFichier(prenom, new char[] {';'})) {
            if(length(prenom)>50) {
                println("Erreur de saisie, veuillez entrer un nom à 50 caractères ou moins ");
            } 
            if (caractereImpossibleFichier(prenom, new char[] {';'})) {
                println("Caractère ';' non accepté. Veuillez ne pas entrer de ; dans votre nom");
            }
            delay(100);
            print("Ton nom: ");
            prenom = readString();
        }

        return prenom;
    }

    int choixAction () { // réalise la saisie de l'action voulue par le joueur et renvoie le numéro du choix correspondant (1 pour attaquet et 2 pour soigner)
        //Option du joueur
        printlnTmp("Que souhaitez vous faire ?");
        delay(200);
        println("1. Attaquer (utilise 1 tour)");
        delay(200);
        println("2. Se soigner (utilise 1 tour)");
        delay(200);
        println("3. Prendre la fuite (quitte la partie)");
        println();

        // vérification de la saisie joueur
        return choixIntervalle('1','3',"Veuillez entrer un chiffre entre 1 (Attaquer), 2 (Se soigner) ou 3 (Fuir): ");
    }

    //-------------------------------------------FONCTION DE QUESTIONS----------------------------------------------------

    // fonction qui sert à récupérer les questions
    String[][] recupererQuestion(String nomFichier){
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS + nomFichier + ".csv", ';');
        String[][] question = new String[rowCount(fichier)][columnCount(fichier)];
        for(int lignes = 0; lignes < rowCount(fichier); lignes++){
            for(int colonnes = 0; colonnes < columnCount(fichier); colonnes++){
                question[lignes][colonnes] = getCell(fichier, lignes, colonnes);
            }
        }
        return question;
    }

    // fonction qui affiche et pose la question au joueur
    String affichageQuestion(String[][] QUESTIONS, int numQuestion){
        String question = "";
        for(int i = 0; i < 4; i++){
            if(i != 0){
                question += " " + i + ". ";
            }
            question += QUESTIONS[numQuestion][i];
            if(i == 1 || i == 2){
                question += " |";
            }
        }
        return question + " : ";
    }

    boolean reponseCorrecte(String[][] QUESTIONS, int numQuestion, int numReponseQuestion){
        return equals(QUESTIONS[numQuestion][4], QUESTIONS[numQuestion][numReponseQuestion]);
    }

    int saisieReponseQuestion(String texteQuestion){
        print("Réponse : ");
        String saisie = readString();
        while (choixPasCorrecteIntervalle(saisie, '1', '3')) {
            println("Veuillez entrer une bonne option de réponse allant de 1 à 3.");
            printTmp("Entrez votre choix: ");
            saisie = readString();
            println();
            delay(500);
        }
        return stringToInt(saisie);
    }

    //-------------------------------------------FONCTION D'ACTIONS----------------------------------------------------

    boolean peutSeSoigner (Pokemon pokemon) { 
        // vérifie si il est utile à un pokémon de se soigner 
        return !(pokemon.vie == pokemon.vieMax || pokemon.vie==0);
    }

    void testPeutSeSoigner () {
        Pokemon poke1 = newPokemon("pokemonTest", "NULL",60, 100, new String[]{"Griffe","Flammèche","Déflagration"}, new int[]{5,10,20}, null);
        assertFalse (peutSeSoigner(poke1));
        poke1.vie = 40;
        assertTrue (peutSeSoigner(poke1));
        poke1.vie = 0; // cas normalement jamais atteignable car ko
        assertFalse (peutSeSoigner(poke1));
    }

    void soin (int vieGagnee, Pokemon pokemonJoue) {
        //soigne le pokémon si cela est nécessaire
        int newVie;
        newVie = pokemonJoue.vie + vieGagnee;
        if (newVie > pokemonJoue.vieMax) {
            pokemonJoue.vie = pokemonJoue.vieMax;
        } else {
            pokemonJoue.vie = newVie;
        }
    }

    int questionSoin (){
        String saisieSoin = "";
        int choixSoin = 0;

        printlnTmp("De combien souhaitez-vous soigner votre pokemon ?");
        println();
        println("1 : Soignez votre pokemon de 15 PV (Question de difficulté facile) !");
        delay(200);
        println("2 : Soignez votre pokemon de 30 PV (Question de difficulté moyenne) !");
        delay(200);
        println("3 : Soignez votre pokemon de 50 PV (Question de difficulté difficile) !");
        delay(200);
        choixSoin= choixIntervalle('1','3',"Veuillez entrer un chiffre pour vous soigner allant de 1 à 3.");
        // Créer une fonction qui avec un random pose une question boolean poseQuesion(int difficulté);
        return choixSoin;
        
    }

    void poseQuestionSoin(int difficulte, Pokemon pokemonJoue){ //pose la question en séléctionnant dans le fichier csv en question, une ligne aléatoire et modifie soinRetour
        String[][] questions;
        int quantiteSoin;
        if(difficulte == 1){ 
            questions = QUESTION_FACILE;
            quantiteSoin = 15;
        } else if ( difficulte == 2) {
            questions = QUESTION_MOYENNE;
            quantiteSoin = 30;
        } else {
            questions = QUESTION_DIFFICILE;
            quantiteSoin = 50;
        }
        int numQuestion = (int) (random()*length(questions, 1));
        String texteQuestion = affichageQuestion((questions), numQuestion);
        printlnTmp(texteQuestion);
        if(reponseCorrecte(questions, numQuestion, saisieReponseQuestion(texteQuestion))){
            printlnTmp("Félicitation vous avez trouvé juste, " + pokemonJoue.nom + " va pouvoir se restaurer de "+quantiteSoin+" PV");
            soin(quantiteSoin, pokemonJoue);
            delay(1000);
        } else{
            printlnTmp("Vous n'avez pas trouvé la bonne réponse !!");
            printlnTmp("Pendant que vous essayez de soigner " + pokemonJoue.nom + ", l'adversaire a eu le temps de charger son attaque !");
        }
    }

    void testSoin () {
        Pokemon poke1 = newPokemon("pokemonTest", "NULL",60, 100, new String[]{"Griffe","Flammèche","Déflagration"}, new int[]{5,10,20}, null);
        soin(10,poke1);
        assertEquals (60, poke1.vie); //cas où le pokémon a toute sa vie

        poke1.vie = 50;
        soin(10,poke1);
        assertEquals (60, poke1.vie); // cas où la vie est remplie jusqu'à vie max tout pile

        poke1.vie=40;
        soin(50,poke1);
        assertEquals (60, poke1.vie); //cas où la vie + soin dépasse la juge de vie max du pokémon

        poke1.vie=10;
        soin(10,poke1);
        assertEquals (20, poke1.vie); // cas où la vie soignée n'atteint pas la vie max
    }

    boolean KO (int degats, int vie) { 
        // vérifie si un pokémon va être ko suite à une attaque
        if (vie - degats > 0) {
            return false;
        } else {
            return true;
        }
    }

    void testKO() {
        assertTrue (KO(50,20));
        assertTrue (KO(20,20));
        assertFalse (KO(20,21));
        assertFalse (KO(50,100));
    }

    void attaque (int degats, Pokemon cible) {
        //réalise une attaque de n dégats sur le pokémon cible (ennemi ou pokemon du joueur ! )
        if (KO(degats, cible.vie)) { // le pokémon sera dans tous les cas KO après l'attaque si il est positif à cette condition
            cible.ko = true;
            cible.vie = 0;
        } else { // il lui restera des pv (il ne sera pas ko après l'attaque)
            cible.vie = cible.vie - degats;
        }
    }

    void testAttaque () {
        Pokemon cible1 = newPokemon("pokemonTest", "NULL",60, 100, new String[]{"Griffe","Flammèche","Déflagration"}, new int[]{5,10,20}, null);
        attaque (60, cible1);
        assertTrue (cible1.ko);
        assertEquals (cible1.vie, 0);
        Pokemon cible2 = newPokemon("pokemonTest", "NULL",60, 100, new String[]{"Griffe","Flammèche","Déflagration"}, new int[]{5,10,20},null);
        attaque (30, cible2);
        assertFalse (cible2.ko);
        assertEquals (cible2.vie, 30);
        Pokemon cible3 = newPokemon("pokemonTest", "NULL",60, 100, new String[]{"Griffe","Flammèche","Déflagration"}, new int[]{5,10,20},null);
        attaque (80, cible3);
        assertTrue (cible3.ko);
        assertEquals (cible3.vie, 0);

    }

    void poseQuestionAttaque(int difficulte, Pokemon pokemonJoue, Pokemon ennemi){
        int choixAttaque = difficulte + 1; 
        if(choixAttaque == 1){ //pose la question de difficulté séléctionner
            printlnTmp("Vous choissisez la facilité, mais allez vous trouver la bonne réponse ?");
            delay(500);
            int numQuestion = (int) (random()*length(QUESTION_FACILE, 1)); // Prend une question au hasard
            String texteQuestion = affichageQuestion((QUESTION_FACILE), numQuestion);
            printlnTmp(texteQuestion); // Affiche la question
            if(reponseCorrecte(QUESTION_FACILE, numQuestion, saisieReponseQuestion(texteQuestion))){ // Si la réponse est correcte
                printlnTmp("Félicitation vous avez trouvé juste, " + afficheNomPokemon(pokemonJoue)+ " va pouvoir effectuer son attaque !");
                attaque(pokemonJoue.puissanceAttaques[difficulte], ennemi); // réalise l'attaque en fonction de la question
                printlnTmp(afficheNomPokemon(pokemonJoue) + " utilise l'attaque " + pokemonJoue.attaques[difficulte] + " !!!");
                delay(500);
            }
            else{ // Si la réponse n'est pas celle attendu
                printlnTmp("Vous n'avez pas trouvé la bonne réponse !!");
                printlnTmp("Votre attaque légére n'est absolument pas puissante...");
                printlnTmp(afficheNomPokemon(ennemi) + " l'a ressenti comme une légére brise de vent.");
                printlnTmp("Il se moque de vous et ne perd pas de temps à vous attaquer !");
                delay(500);
            }
        }
        if(choixAttaque == 2){ //pose la question de difficulté séléctionner
            printlnTmp("Prenez votre temps, une question comme celle-ci peut s'averer plus dur qu'on ne le pense !");
            delay(1000);
            int numQuestion = (int) (random()*length(QUESTION_MOYENNE, 1)); // Prend une question au hasard
            String texteQuestion = affichageQuestion((QUESTION_MOYENNE), numQuestion);
            printlnTmp(texteQuestion); // Affiche la question
            if(reponseCorrecte(QUESTION_MOYENNE, numQuestion, saisieReponseQuestion(texteQuestion))){ // Si la réponse est correcte
                printlnTmp("Félicitation vous avez trouvé juste, " + afficheNomPokemon(pokemonJoue) + " va pouvoir effectuer son attaque !");
                attaque(pokemonJoue.puissanceAttaques[difficulte], ennemi); // réalise l'attaque en fonction de la question
                printlnTmp(afficheNomPokemon(pokemonJoue) + " utilise l'attaque " + pokemonJoue.attaques[difficulte] + " !!!");
                delay(500);
            }
            else{ // Si la réponse n'est pas celle attendu
                printlnTmp("Vous n'avez pas trouvé la bonne réponse !!");
                printlnTmp("Votre attaque échoue complétement et vous visez a côté ");
                printlnTmp("Un groupe de noeunoeuf est touché a la place et prend la fuite...");
                printlnTmp("Apprenez à mieux viser la prochaine fois !");
                delay(500);
                printlnTmp(afficheNomPokemon(ennemi) + " a pu se prépare à riposter !");
                delay(500);
            }
        }
        if(choixAttaque == 3){ //pose la question de difficulté séléctionner
            printlnTmp("Vous n'avez pas peur de la difficulté !");
            printlnTmp("Voici maintenant la question, bon courage !");
            delay(500);
            int numQuestion = (int) (random()*length(QUESTION_DIFFICILE, 1)); // Prend une question au hasard
            String texteQuestion = affichageQuestion((QUESTION_DIFFICILE), numQuestion);
            printlnTmp(texteQuestion); // Affiche la question
            if(reponseCorrecte(QUESTION_DIFFICILE, numQuestion, saisieReponseQuestion(texteQuestion))){ // Si la réponse est correcte
                printlnTmp("Félicitation vous avez trouvé juste, " + afficheNomPokemon(pokemonJoue) + " va pouvoir effectuer son attaque !");
                attaque(pokemonJoue.puissanceAttaques[difficulte], ennemi); // réalise l'attaque en fonction de la question
                printlnTmp(afficheNomPokemon(pokemonJoue) + " utilse l'attaque " + pokemonJoue.attaques[difficulte] + " !!!");
                delay(500);
            }
            else{ // Si la réponse n'est pas celle attendu
                printlnTmp("Vous n'avez pas trouvé la bonne réponse !!");
                printlnTmp("Votre attaque lourde a échoué... ");
                printlnTmp(afficheNomPokemon(ennemi) + " vous regarde avec joie, se moque et vous attaque !!");
                delay(500);
            }
        }
    }

    int probaEnnemi (Pokemon ennemi) { // renvoie aléatoirement l'indice d'une attaque possible de l'ennemi en fonction des probabilités
        int numAttaque;
        int probaAttaque = (int) (random() * 11);
        if(probaAttaque <= 2) { // si le pokémon loupe son attaque !!
            numAttaque = -1;
        } else if (probaAttaque <= 5) {
            numAttaque = 0; //attaque faible
        } else if (probaAttaque <=8) {
            numAttaque = 1; //attaque moyenne
        } else {
            numAttaque = 2; //attaque forte
        }
        return numAttaque;

    }

    void attaqueEnnemi (Pokemon ennemi, Pokemon pokemonJoue) {
        int numAttaque = probaEnnemi(ennemi);
        if (numAttaque < 0) { //lorque l'adversaire loupe son attaque
            printTmp (afficheNomPokemon(ennemi) + " glisse et loupe son attaque !");
            printlnTmp(" Cela ne vous a fait aucun effet...");
        } else {
            printlnTmp(afficheNomPokemon(ennemi) + " utilise " + ennemi.attaques[numAttaque] + " et vous inflige " + ennemi.puissanceAttaques[numAttaque]+ " de dégâts !!!");
            attaque(ennemi.puissanceAttaques[numAttaque], pokemonJoue);
        }
    }

    Pokemon ajoutExperience (Pokemon pokemonJoue, int nombreTour, int ennemisNumero){ // ajoute de l'expérience du pokémon en fonction du nombre de tours qu'il lui a fallut
        int nbExp;
        if(nombreTour <= 3){
            nbExp = 40;
        }else if (nombreTour <= 5) {
            nbExp = 30;
        }else if (nombreTour <= 7) {
            nbExp = 20;
        } else {
            nbExp = 10;
        }
        if (ennemisNumero == 4) { // un boss est battu
            nbExp = nbExp*2; //il reçoit donc le double d'exp qu'il devait obtenir
        }
        printlnTmp (afficheNomPokemon(pokemonJoue) + " gagne " + nbExp + " exp !");
        pokemonJoue.experience = pokemonJoue.experience + nbExp;
        return evolution (pokemonJoue); // après avoir ajouté l'expérience, on vérifie si le pokémon peut évoluer, si oui, il remplace le pokemon actuel
    }

    Pokemon evolution (Pokemon pokemonJoue) {
        //fait évoluer le pokémon du joueur si cela est possible

        if (pokemonJoue.experience >= pokemonJoue.experienceMax && pokemonJoue.evolution !=null) {
            println();
            printTmp("Oh.. ");
            printlnTmp("Mais..");
            delay(100);
            printTmp("Votre pokémon évolue en "+ afficheNomPokemon(pokemonJoue.evolution) + "!! ");
            delay(100);
            printlnTmp("Il semble ravi de sa nouvelle apparence !");
            delay(500);
            println();
            Pokemon pokemonEvolue = pokemonJoue.evolution;
            pokemonEvolue.experience = pokemonJoue.experience;
            pokemonEvolue.vie = (int) (pokemonJoue.vie * pokemonEvolue.vieMax / pokemonJoue.vieMax); // le petit produit en croix des familles fera l'affaire
            pokemonJoue = pokemonEvolue;
        }
        return pokemonJoue;
    }

    //---------------------------------------------FONCTION DE JEU------------------------------------------------------

    String rencontre () { 
        // Rencontre avec le professeur et premiers dialogues du jeu: renvoie le nom du joueur qu'il saisira

        String nomJoueur; //création de la variable du nom du joueur;

        clearScreen();
        println(chargerRessourceMonde("Logo PokeQuiz.txt"));
        println();
        println(chargerRessourceMonde("professeur Chen.txt"));
        print("Professeur Chen: ");
        delay(500);
        printlnTmp("Bonjour jeune dresseur.se !");
        print("Professeur Chen: ");
        delay(500);
        printlnTmp("Comment t'appelles-tu ?");

        nomJoueur=choixPrenom();
        //clearScreen();
        compteur_car=0;
        return nomJoueur;
    }

    Pokemon dialogueChoixStarter (String nomJoueur) { 
        //renvoie le Starter avec lequel le joueur jouera

        Pokemon pokemonJoue; // création de la variable du pokémon qui sera joué pendant l'aventure
        Pokemon bulbizarre = getPokemon(STARTERSCSV,STARTERS,"Bulbizarre");
        Pokemon salameche = getPokemon(STARTERSCSV,STARTERS,"Salamèche");
        Pokemon carapuce = getPokemon(STARTERSCSV,STARTERS,"Carapuce");
        Pokemon pichuwu = getPokemon(STARTERSCSV,STARTERS,"Pichuwu");


        print("Professeur Chen: ");
        delay(500);
        printlnTmp("Enchanté " + nomJoueur + " !");

        //choix du starter
        print("Professeur Chen: ");
        delay(500);
        printTmp("Il est temps pour toi de choisir un pokémon pour débuter ton long périple ! ");
        printlnTmp("Lequel veux-tu ?");
        delay(500);
        printlnTmp("1."+ afficheNomPokemon(bulbizarre)); //petit kiffe ptdrrr
        printlnTmp("2."+ afficheNomPokemon(salameche));
        printlnTmp("3."+ afficheNomPokemon(carapuce));
        delay(100);
        pokemonJoue = choixStarter(bulbizarre, salameche, carapuce, pichuwu);
        print("Professeur Chen: ");
        delay(500);
        printlnTmp("Tu as choisi "+afficheNomPokemon(pokemonJoue)+" !");
        print("Professeur Chen: ");
        printTmp("Tu es désormais prêt.te à partir à l'aventure ! ");
        printlnTmp("Bon courage !!");
        delay(1000);
        clearScreen();
        return pokemonJoue;
    }

    boolean choixChargerEcraser() { //retourne true si le joueur souhaite charger la partie et false si il souhaite écraser sa sauvegarde et comencer une nouvelle partie
        println();
        printlnTmp (ANSI_FAINT+ANSI_ITALIC+ "Une sauvegarde à votre nom a été trouvée");
        printlnTmp (ANSI_FAINT+ANSI_ITALIC+ "Voulez-vous l'utiliser ou faire une nouvelle partie ?");
        println(ANSI_RESET+ANSI_ITALIC+"1. Charger la partie");
        println("2. Nouvelle partie");
        println();
        int choix = choixIntervalle ('1','2', "Erreur, veuiler entrer 1 ou 2.");
        if (choix == 1) {
            return true;
        } else {
            return false;
        }
    }

    Pokemon remplirCSVNewPokemon(String nomJoueur) {
        Pokemon pokemonJoue;
        creationSauvegarde (nomJoueur);
        pokemonJoue = dialogueChoixStarter (nomJoueur); //dialogue de choix du starter avec le professeur Chen
        modifierCSV(nomJoueur,1,pokemonJoue.nom); //nom du pokémon
        modifierCSV(nomJoueur,2,""+pokemonJoue.vie); //vie du pokémon
        modifierCSV(nomJoueur,3,""+pokemonJoue.experience); //nombre d'exp du pokémon
        modifierCSV (nomJoueur,4,"0"); //numéro de l'ennemi à battre dans le tableau du monde
        return pokemonJoue;
    }

    Pokemon chargerPokemonOuChoisirPokemon (String nomJoueur) {
        Pokemon pokemonJoue;
        if (fichierCSVExiste(nomJoueur)) {
            if (stringToInt(chargerCSV(nomJoueur,4)) != -1 && choixChargerEcraser()) {
                printTmp (ANSI_FAINT+"Chargement de la sauvegarde .");
                printTmp (".");
                printTmp ("."+ANSI_RESET+ANSI_WHITE);
                pokemonJoue = chargerPokemon (nomJoueur);
                
            } else {
                printTmp (ANSI_FAINT+"Ecrasement de la sauvegarde .");
                printTmp (".");
                printlnTmp ("."+ANSI_RESET+ANSI_WHITE);
                println();
                pokemonJoue = remplirCSVNewPokemon(nomJoueur);
            }
            clearScreen();

        } else {
            pokemonJoue = remplirCSVNewPokemon(nomJoueur);
        }
        return pokemonJoue;

    }

    int soignerSiPossible (Pokemon pokemonJoue, Pokemon ennemi) {  
        //Si c'est possible soigne le pokémon et donc passe à la suite (en conservant come choix d'action de 2), ou sinon renvoie le choix d'action 1 pour switcher sur le choix des attaques

        if(pokemonJoue.vie < pokemonJoue.vieMax){
            clearScreen();
            printlnTmp("Vous decidez de soigner " + afficheNomPokemon(pokemonJoue) + " !!");
            afficheAsciiPokemon (pokemonJoue);
            println("Votre pokemon : " + afficheNomPokemon(pokemonJoue) + " (vie " + pokemonJoue.vie + " / " + pokemonJoue.vieMax + ")" +  " (exp " + pokemonJoue.experience + " / " + pokemonJoue.experienceMax + ")\n");
            // Appel de la fonction poseQuestion pour poser une question en fonction de la difficulté
            int difficulte = questionSoin();
            poseQuestionSoin(difficulte, pokemonJoue);
            return 2; //le joueur conserve comme choix le numéro 2: soigner son pokémon

        }else{
            printlnTmp("Vous possédez déja le nombre de PV maximum, vous ne pouvez qu'attaquer !");
            delay(500);
            return 1; //le joueur est renvoyé sur l'option 1: attaquer
        }
    }

    void attaqueCombat (Pokemon pokemonJoue, Pokemon ennemi) { // réalise l'attaque choisie par le joueur de son pokémon sur l'adversaire 

        //Affichage des attaques disponibles
        afficheAttaques(pokemonJoue);
        println();

        //Choix de l'attaque grâce à la fonction de choix: choixAttaquePokemon(pokemonJoue) et conversion en int 
        int attaqueChoisie = choixIntervalle('1', '3',"Veuillez entrer un chiffre allant de 1 à 3.")-1; // il faut décrémenter de un le résultat car dans le tableau les indices sont compris entre 0 et 2

        //Attaque
        poseQuestionAttaque(attaqueChoisie, pokemonJoue, ennemi);
        
    }

    Pokemon tourEnnemi (Pokemon pokemonJoue, Pokemon ennemi, int nombreTour, int ennemisNumero) {
        //permets à l'adversaire d'attaquer à son tour dans le cas où il n'est pas ko (logique)

        if (!ennemi.ko) {
            //risposte de l'ennemi
            attaqueEnnemi(ennemi, pokemonJoue);
        } else {
            printlnTmp("Félicitation, vous avez vaincu " + afficheNomPokemon(ennemi) + " en " + (nombreTour+1) + " tour(s) !!");
            pokemonJoue = ajoutExperience(pokemonJoue, nombreTour, ennemisNumero);
        }
        return pokemonJoue;
    }

    boolean combatPokemonEnnemi (Pokemon pokemonJoue, Pokemon ennemi, int ennemisNumero, String nomJoueur) { // réalise le combat entre un adversaire et le pokémon du joueur jusqu'à ce que l'un d'entre eux soit ko
        printTmp(ANSI_ITALIC +"Un pokemon sauvage vous attaque, ");
        printlnTmp("d'après votre pokédex il s'agit de : " + afficheNomPokemon(ennemi) + " !!"+ANSI_RESET+ANSI_WHITE);
        delay(2000);
        clearScreen();
        int nombreTour = stringToInt(chargerCSV(nomJoueur,7));

        //boucle de combat
        while (!pokemonJoue.ko && !ennemi.ko){
            if (nombreTour>0) { // rend le terminal plus propre mais uniquement quand il ne s'agit pas du premier tout avec le pokémon
                delay(1000);
                clearScreen();
            }
            println();
            println(ANSI_GREEN+chargerRessourceMonde("HautesHerbes.txt")+ANSI_RESET+ANSI_WHITE);
            println("Tour " + (nombreTour+1) + " :");
            afficheAsciiPokemon (pokemonJoue);
            println("Votre pokemon : " + afficheNomPokemon(pokemonJoue) + " (vie " + afficherVie(pokemonJoue) + " / " + pokemonJoue.vieMax + ")" +  " (exp " + pokemonJoue.experience + " / " + pokemonJoue.experienceMax + ")\n");
            afficheAsciiPokemon (ennemi);
            println("                                                            Ennemi : " + afficheNomPokemon(ennemi) + " (" + afficherVie(ennemi)+ " / " + ennemi.vieMax + ")\n");
            println(ANSI_GREEN+chargerRessourceMonde("HautesHerbes.txt")+ANSI_RESET+ANSI_WHITE);
            int optionChoisie = (choixAction()); // conversion directe en nombre de la valeur entrée par l'utilisateur qui correspond à attaque ou soin
            println();
            if (optionChoisie == 3) {
                printTmp (ANSI_ITALIC+ANSI_FAINT+ "Êtes-vous sûrs de vouloir quitter le combat ? ");
                println("(vos données seront sauvegardées)"+ ANSI_RESET+ANSI_WHITE);
                delay(100);
                println("1. Oui");
                println("2. Non");
                println();
                int choixQuitter = choixIntervalle('1', '2', "veuillez choisir un nombre entre 1 (oui) et 2 (non)");
                if (choixQuitter == 1) {
                    println();
                    printlnTmp("Vous et votre pokémon décidez de prendre la fuite !!");
                    delay(1000);
                    return true;
                }
            } else {
                // Si Se soigner choisie
                if(optionChoisie == 2 ){
                    optionChoisie = soignerSiPossible (pokemonJoue, ennemi); //soigne le pokémon si c'est possible et sinon renvoie le joueur d=sur l'option 1 grâce au return de la fonction
                }
                // Si attaque choisie
                if(optionChoisie == 1){
                    attaqueCombat(pokemonJoue, ennemi); //permettra au joueur de choisir l'attaque voulue 
                }
                println();
                pokemonJoue = tourEnnemi(pokemonJoue, ennemi, nombreTour, ennemisNumero); // réalise les actions de l'adversaire si il n'est pas KO et vérifie si on passe à l'enemi suivant dans le return
                nombreTour++;
                delay(1000);
            }
            sauvegardeCombat (nomJoueur, pokemonJoue, ennemi, nombreTour);
            modifierCSV (nomJoueur,5,""+ennemisNumero);
        }
        clearScreen();
        return false;
    }

    boolean lancementCombat(Pokemon pokemonJoue, Monde monde, String nomJoueur) { // j'ai split ta fonction en plein de petites fontions, tu me diras ce que t'en penses !
        // numéro de l'ennemi a affronter
        println(chargerRessourceMonde(monde.nom+".txt"));
        printlnTmp("Bienvenue dans la ville : " +ANSI_ITALIC+ANSI_BOLD+monde.nom+ANSI_RESET+ANSI_WHITE);
        delay(2000);
        clearScreen();
        int ennemisNumero = stringToInt(chargerCSV (nomJoueur,5));
        boolean stop = false;

        //Parcours tout les ennemis dans la liste monde
        while(ennemisNumero < length(monde.ennemis) && !pokemonJoue.ko && !stop){
            Pokemon ennemi = monde.ennemis[ennemisNumero];
            stop = combatPokemonEnnemi(pokemonJoue, ennemi, ennemisNumero, nomJoueur);//combat entre le pokémon et l'adversaire jusque l'un des deux soit ko
            pokemonJoue = chargerPokemon (nomJoueur);
            if (stop) {
                return stop;
            } else {
                if (pokemonJoue.ko) { //Le joueur a perdu 
                    printlnTmp("Oh non ! Votre pokémon est ko !");
                    printlnTmp(afficheNomPokemon(ennemi) + " vous regarde d'un air satisfait, et s'en va");
                    printlnTmp("Vous avez perdu...");



                } else {
                    if (ennemi.ko) { //on vérifie si à la sortie de la fonction de combat c'est l'ennemi qui est ko
                        ennemisNumero++; // si c'est ca on passe à l'ennemi suivant
                        modifierCSV (nomJoueur,5,""+ennemisNumero);
                        modifierCSV (nomJoueur,7,"0");
                    }
                }
            }
        }
        modifierCSV (nomJoueur,5,"0");
        modifierCSV (nomJoueur,6,"NULL");
        return stop;
    }

    boolean lancementCombat(Pokemon pokemonJoue, Monde monde, Monde mondeSuivant, String nomJoueur) {
        boolean stop = lancementCombat (pokemonJoue, monde, nomJoueur);
        if(!pokemonJoue.ko && !stop){
            printlnTmp(ANSI_FAINT+"Félicitation, vous êtes parvenu.e a la fin de la ville " + ANSI_ITALIC + monde.nom + ANSI_RESET +ANSI_WHITE+ ", vous passez donc a la ville : " + ANSI_ITALIC + mondeSuivant.nom + ANSI_RESET +ANSI_WHITE+ " !!");
            delay(1000);
            clearScreen();
        }
        return stop;
    }

    void aide () {
        //écran d'aide au joueur
        clearScreen();
        chargerFichier("./ressources/aide.txt");
        print("Appuyez sur entrer pour retourner au menu principal ");
        readString();
    }


    void algorithm(){
        Pokemon pokemonJoue;
        String nomJoueur;

        // String multicolor = ANSI_RED + "regarde "+ANSI_YELLOW + "j'ai "+ANSI_GREEN + "réussi "+ANSI_BLUE + "youhou "+ANSI_PURPLE + "c'est "+ANSI_WHITE+ "trop "+ ANSI_CYAN + "bien" + ANSI_RESET;
        // printlnTmp(multicolor);

        text(ANSI_WHITE); // paramètres de base le l'affichage console
        //penser aussi à choisir un fond pour la console en noir
        clearScreen();
        println(chargerRessourceMonde("Logo PokeQuiz.txt"));
        println();

        int etatMenu = 0; // 0= Choix de base, 1=Jeu, 2=Aide, 3=Quitter

        while(etatMenu != 3){ //Quitter le jeu
            etatMenu = choixMenu();
            switch(etatMenu){
                case 1 : //Jeu
                    //lancement du jeu
                    clearScreen();
                    nomJoueur= rencontre(); //rencontre avec le professeur Chen et saisie du prénom
                    pokemonJoue = chargerPokemonOuChoisirPokemon (nomJoueur);
                    //accès aux mondes
                    boolean stop = false;
                    int indiceMonde = stringToInt(chargerCSV(nomJoueur,4));
                    while (indiceMonde < length(continent) && !stop) {
                        if (indiceMonde == length(continent)-1) {
                            stop = lancementCombat(pokemonJoue, continent[indiceMonde],nomJoueur);
                            pokemonJoue = chargerPokemon (nomJoueur);
                            clearScreen();
                            if (stop) {
                                break;
                            } else {
                                printTmp("Félicitations !");
                                printlnTmp("Vous avez battus tous les pokémons sur votre chemin et terminé votre aventure !");
                                println();
                                printTmp("Merci d'avoir joué à PokéQuiz Adventures !");
                            }
                        } else {
                            stop = lancementCombat(pokemonJoue, continent[indiceMonde], continent[indiceMonde + 1],nomJoueur);
                            pokemonJoue = chargerPokemon (nomJoueur);
                            if (pokemonJoue.ko || stop ) {
                                if (pokemonJoue.ko) {
                                    creationSauvegarde(nomJoueur);
                                }
                                break;
                            }
                        }
                        indiceMonde ++;
                        modifierCSV(nomJoueur,4,""+indiceMonde);
                        modifierCSV (nomJoueur,5,""+0);
                    }
                    break;

                case 2 : //Aide
                    aide();
                    break;
            }
        }
    }
}