/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Outils;

/**
 *
 * @author nicod
 */
public class TestAsciiGraphique {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int width = 20;  // Largeur du rectangle
        int height = 5;  // Hauteur du rectangle

        // Ligne supérieure
        System.out.print("\u001B[0m"+"\u250C"+"\u001B[31m"); // Coin supérieur gauche
        for (int i = 0; i < width; i++) {
            System.out.print("\u2500"); // Ligne horizontale
        }
        System.out.println("\u2510"); // Coin supérieur droit

        // Lignes verticales
        for (int i = 0; i < height; i++) {
            System.out.print("\u2502"); // Ligne verticale gauche
            for (int j = 0; j < width; j++) {
                System.out.print(" "); // Espace vide à l'intérieur
            }
            System.out.println("\u2502"); // Ligne verticale droite
        }

        // Ligne inférieure
        System.out.print("\u2514"); // Coin inférieur gauche
        for (int i = 0; i < width; i++) {
            System.out.print("\u2500"); // Ligne horizontale
        }
        System.out.println("\u2518"); // Coin inférieur droit
        
        
        System.out.println( "\u2554\u2550\u2564\u2550\u2557" );
        System.out.println( "\u2551 \u2502 \u2551" );
        System.out.println( "\u255f\u2500\u253c\u2500\u2562" );
        System.out.println( "\u2551 \u2502 \u2551" );
        System.out.println( "\u255a\u2550\u2567\u2550\u255d" );
        System.out.println(  );
         
        // ASCII alternative
        System.out.println( "+----+----+----+----+" );
        System.out.println( "|  1 |    | 22 |    |" );
        System.out.println( "+----+----+----+----+" );
        System.out.println( "|    | 97 |  3 |    |" );
        System.out.println( "+----+----+----+----+" );

        System.out.println();
        Data.afficheTousLesCodes();
        System.out.println();
        Data.afficheUnCodeAvecCouleur(10, Data.RED); 
        System.out.println();
        Data.afficheUnCodeAvecCouleur(11, Data.BLUE); 
        System.out.println();
    }
    
}
