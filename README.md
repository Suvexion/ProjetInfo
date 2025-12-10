# ProjetInfo









TUILE :





/\*

 \* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license

 \* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

 \*/

package netwalk;



/\*\*

 \*

 \* @author thua

 \*/

public class Tuile {



 



    // --- 1. ATTRIBUTS ---

    private int codeCase;

    private Type type; // Utilisation de l'enum

    private boolean estConnectee = false;



    // --- ENUMÉRATION DU TYPE ---

    public enum Type {

        SOURCE,

        TERMINAL,

        TUYAU

    }

    // --- 2. CONSTANTES BINAIRES (Masque 8-4-2-1 / Anti-horaire) ---

    // NORD : Bit de poids fort (MSB) -> 8

    public static final int NORD  = 8;  // 1000

    public static final int EST   = 4;  // 0100

    public static final int SUD   = 2;  // 0010

    public static final int OUEST = 1;  // 0001

 

    // --- 3. CONSTRUCTEUR ---

    public Tuile(int connexionsInitiales, Type type, boolean estConnectee) { // Typage de l'enum

        this.codeCase = connexionsInitiales;

        this.type = type;

        this.estConnectee = estConnectee;

    }

 

    // --- 4. MÉTHODE DE ROTATION (Anti Horaire) ---

    public void fairePivoter() {

        if (this.type == Type.SOURCE) return;

 

    // Si la tuile est ouverte au Nord (8), la rotation la mène à 16.

    this.codeCase = this.codeCase \* 2;

 

    // Si dépassement (16 ou plus), on enlève le 16 et on ramène le bit perdu à la position OUEST (1).

    if (this.codeCase > 15) {

        // Le 16 n'existe pas en 4 bits, il doit réintégrer le bas (Ouest=1).

        this.codeCase = (this.codeCase % 16) + 1; // Ex: 16%16 = 0, +1 = 1.

    }

    }

 

 

    // --- 6. GETTERS/SETTERS ---

    public Type getType() {

         return type;

        }

    public int getConnexions() {

         return codeCase; } // Ajout du getter manquant pour les tests

    public void setConnectee(boolean etat) {

        this.estConnectee = etat;

    }

    public boolean getEstConnectee() {

        return estConnectee;

    }



 

    @Override

    public String toString() {

 

 

    //Tuile vide

    if (this.codeCase==0){

        return "";



    }





    //Ligne simple

    if (this.codeCase==10){ //Verticale

        return "\\u2502";



    }



    if (this.codeCase==5){ //Horizontale

        return "\\u2500";



    }



    //Coude



    if (this.codeCase==12){ //L

        return "\\u2514";



    }

 

    if (this.codeCase==6){ //r

        return "\\u250C";



    }



    if (this.codeCase==3){ //7

        return "\\u2510";



    }

 

    if (this.codeCase==3){ //J

        return "\\u2518";



    }



    //Jonctions en T



    if (this.codeCase==14){ //Manque ouest

        return "\\u2524";



    }



    if (this.codeCase==7){ //Manque nord

        return "\\u2534";



    }



    if (this.codeCase==11){ //Manque est

        return "\\u251C";



    }



    if (this.codeCase==13){ //Manque sud

        return "\\u252C";



    }



    //Jonction en croix



    if (this.codeCase==15){ //croix

        return "\\u253C";



    }



    else {

        return "X";



    }



}

}









Tuile : 





import java.util.Random; // Utile pour la génération aléatoire future





public class Jeu {



&nbsp;   // --- 1. ATTRIBUTS ---

&nbsp;   private Tuile\[]\[] grille; // Le tableau 2D qui stocke tous les objets Tuile

&nbsp;   private int lignes;

&nbsp;   private int colonnes;

&nbsp;   

&nbsp;   // Un attribut pour le décompte des coups si tu veux l'afficher

&nbsp;   private int nombreDeCoups = 0; 

&nbsp;   

&nbsp;   // --- 2. CONSTRUCTEUR ---

&nbsp;   public Plateau(int lignes, int colonnes) {

&nbsp;       this.lignes = lignes;

&nbsp;       this.colonnes = colonnes;

&nbsp;       this.grille = new Tuile\[lignes]\[colonnes];

&nbsp;       

&nbsp;   }

&nbsp;   



&nbsp;   @Override

&nbsp;   public String toString() {

&nbsp;       StringBuilder sb = new StringBuilder();

&nbsp;       

&nbsp;       sb.append("Plateau NetWalk (").append(lignes).append("x").append(colonnes).append(") | Coups: ").append(nombreDeCoups).append("\\n");

&nbsp;       

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               // Ici, on demande à chaque tuile de renvoyer son caractère (ex: '┼', '└', etc.)

&nbsp;               // On utilise un espace pour séparer les tuiles

&nbsp;               sb.append(grille\[i]\[j].toConsoleChar()).append(" "); 

&nbsp;           }

&nbsp;           sb.append("\\n"); // Nouvelle ligne après chaque ligne de la grille

&nbsp;       }

&nbsp;       return sb.toString();

&nbsp;   }



&nbsp;   public void faireTournerTuile(int x, int y) {

&nbsp;       if (x >= 0 \&\& x < lignes \&\& y >= 0 \&\& y < colonnes) {

&nbsp;           grille\[x]\[y].fairePivoter();

&nbsp;           this.nombreDeCoups++;

&nbsp;       } else {

&nbsp;           System.err.println("Coordonnées de tuile invalides.");

&nbsp;       }

&nbsp;   }





&nbsp;   private int directionOpposee(int dir) {

&nbsp;       // Dans le masque 1-2-4-8 (rotation horaire) :

&nbsp;       // N(1) opposé à S(4), E(2) opposé à O(8).

&nbsp;       if (dir == Tuile.NORD) return Tuile.SUD;

&nbsp;       if (dir == Tuile.EST) return Tuile.OUEST;

&nbsp;       if (dir == Tuile.SUD) return Tuile.NORD;

&nbsp;       if (dir == Tuile.OUEST) return Tuile.EST;

&nbsp;       return 0; // Erreur

&nbsp;   }



&nbsp;   /\*\*

&nbsp;    \* Vérifie si la connexion entre les deux tuiles adjacentes (x1, y1) et (x2, y2) est valide.

&nbsp;    \*/

&nbsp;   public boolean verifierLien(int x1, int y1, int x2, int y2) {

&nbsp;       // ... (Tuile a) est toujours (x1, y1), (Tuile b) est toujours (x2, y2)



&nbsp;       if (x1 == x2 \&\& y1 == y2 + 1) { // Tuile A est à l'EST de Tuile B

&nbsp;           // A doit avoir une connexion à l'OUEST, B doit avoir une connexion à l'EST.

&nbsp;           return grille\[x1]\[y1].aConnexion(Tuile.OUEST) \&\& grille\[x2]\[y2].aConnexion(Tuile.EST);

&nbsp;       } 

&nbsp;       // ... Il faut ajouter les 3 autres cas (Nord, Sud, Ouest)

&nbsp;       

&nbsp;       return false;

&nbsp;   }

}





