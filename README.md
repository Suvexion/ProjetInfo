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

