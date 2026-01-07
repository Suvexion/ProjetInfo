package netwalk;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TestNEtwalk {

    public static void main(String[] args) {
        // 1. FORCE L'UTF-8
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {}

        // 2. Initialisation
        Jeu jeu = new Jeu(4, 6, System.currentTimeMillis()); 
        Scanner scanner = new Scanner(System.in);
        boolean jouer = true;
        
        // --- CHRONOMÈTRE : On note l'heure de début ---
        long tempsDebut = System.currentTimeMillis();

        while (jouer) {
            // "Nettoyage" console
            System.out.println("\n\n\n\n\n");
            
            // On passe le temps écoulé à la fonction d'affichage
            long tempsEcouleMillis = System.currentTimeMillis() - tempsDebut;
            afficherJeu(jeu, tempsEcouleMillis);

            if (jeu.partieTerminee()) {
                long secondesTotal = (System.currentTimeMillis() - tempsDebut) / 1000;
                System.out.println("\nVICTOIRE !");
                System.out.println("Coups : " + jeu.getNombreDeCoups());
                System.out.println("Temps : " + secondesTotal + " secondes.");
                break;
            }

            System.out.print("Commande (i,j,d pour droite / q pour quitter) : ");
            
            if (scanner.hasNextLine()) {
                String cmd = scanner.nextLine().trim();
                if (cmd.equalsIgnoreCase("q")) {
                    jouer = false;
                    System.out.println("Abandon.");
                } else {
                    try {
                        String[] parts = cmd.split(",");
                        // Accepte aussi juste "i,j" (tourne à droite par défaut) ou "i,j,d"
                        int r = Integer.parseInt(parts[0].trim());
                        int c = Integer.parseInt(parts[1].trim());
                        
                        // Si l'utilisateur tape juste "0,0", on tourne
                        jeu.faireTournerTuile(r, c);
                        
                    } catch(Exception e) {
                        // On ignore les mauvaises saisies pour ne pas planter
                    }
                }
            }
        }
        scanner.close();
    }

    private static void afficherJeu(Jeu jeu, long tempsMillis) {
        Tuile[][] grille = jeu.getGrille();
        int L = jeu.getLignes();
        int C = jeu.getColonnes();

        // Calcul du temps en mm:ss
        long totalSecondes = tempsMillis / 1000;
        long minutes = totalSecondes / 60;
        long secondes = totalSecondes % 60;
        String tempsFormatte = String.format("%02d:%02d", minutes, secondes);

        // Indices Colonnes
        System.out.print("    ");
        for(int j=0; j<C; j++) System.out.print(" " + j + " ");
        System.out.println();

        // Bordure Haut
        System.out.print("    \u250C");
        for(int j=0; j<C; j++) System.out.print("\u2500\u2500\u2500");
        System.out.println("\u2510");

        // Contenu Grille
        for(int i=0; i<L; i++) {
            System.out.print(" " + i + "  \u2502");
            for(int j=0; j<C; j++) {
                System.out.print(" " + grille[i][j] + " ");
            }
            System.out.println("\u2502  " + i);
        }

        // Bordure Bas
        System.out.print("    \u2514");
        for(int j=0; j<C; j++) System.out.print("\u2500\u2500\u2500");
        System.out.println("\u2518");
        
        // --- AFFICHAGE INFOS (Coups + Chrono) ---
        System.out.println("    Coups: " + jeu.getNombreDeCoups() + "   |   Chrono: " + tempsFormatte);
    }
}