/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package netwalk;

/**
 *
 * @author gdarre
 */

import java.util.Random; // Utile pour la génération aléatoire future


public class Jeu {

    // --- 1. ATTRIBUTS ---
    private Tuile[][] grille; // Le tableau 2D qui stocke tous les objets Tuile
    private int lignes;
    private int colonnes;
    
    // Un attribut pour le décompte des coups si tu veux l'afficher
    private int nombreDeCoups = 0; 
    
    // --- 2. CONSTRUCTEUR ---
    public Plateau(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.grille = new Tuile[lignes][colonnes];
        
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Plateau NetWalk (").append(lignes).append("x").append(colonnes).append(") | Coups: ").append(nombreDeCoups).append("\n");
        
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                // Ici, on demande à chaque tuile de renvoyer son caractère (ex: '┼', '└', etc.)
                // On utilise un espace pour séparer les tuiles
                sb.append(grille[i][j].toConsoleChar()).append(" "); 
            }
            sb.append("\n"); // Nouvelle ligne après chaque ligne de la grille
        }
        return sb.toString();
    }

    public void faireTournerTuile(int x, int y) {
        if (x >= 0 && x < lignes && y >= 0 && y < colonnes) {
            grille[x][y].fairePivoter();
            this.nombreDeCoups++;
        } else {
            System.err.println("Coordonnées de tuile invalides.");
        }
    }


    private int directionOpposee(int dir) {
        // Dans le masque 1-2-4-8 (rotation horaire) :
        // N(1) opposé à S(4), E(2) opposé à O(8).
        if (dir == Tuile.NORD) return Tuile.SUD;
        if (dir == Tuile.EST) return Tuile.OUEST;
        if (dir == Tuile.SUD) return Tuile.NORD;
        if (dir == Tuile.OUEST) return Tuile.EST;
        return 0; // Erreur
    }

    /**
     * Vérifie si la connexion entre les deux tuiles adjacentes (x1, y1) et (x2, y2) est valide.
     */
    public boolean verifierLien(int x1, int y1, int x2, int y2) {
        // ... (Tuile a) est toujours (x1, y1), (Tuile b) est toujours (x2, y2)

        if (x1 == x2 && y1 == y2 + 1) { // Tuile A est à l'EST de Tuile B
            // A doit avoir une connexion à l'OUEST, B doit avoir une connexion à l'EST.
            return grille[x1][y1].aConnexion(Tuile.OUEST) && grille[x2][y2].aConnexion(Tuile.EST);
        } 
        // ... Il faut ajouter les 3 autres cas (Nord, Sud, Ouest)
        
        return false;
    }
}

