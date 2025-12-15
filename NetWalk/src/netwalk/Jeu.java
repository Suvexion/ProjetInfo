package netwalk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import netwalk.Tuile.Type;

public class Jeu {

    private Tuile[][] grille;
    private int lignes;
    private int colonnes;
    private int nombreDeCoups = 0;
    private Random random = new Random();

    public Jeu(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.grille = new Tuile[lignes][colonnes];
        
        // 1. Générer un réseau parfait (Arbre couvrant)
        genererNiveau(); 
        
        // 2. Mélanger les pièces
        melangerTuiles();
    }

    private void genererNiveau() {
        // Remplir de cases vides
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                grille[i][j] = new Tuile(0, Type.TUYAU, false);
            }
        }
        
        // Creuser le labyrinthe depuis le centre
        int startX = lignes / 2;
        int startY = colonnes / 2;
        boolean[][] visite = new boolean[lignes][colonnes];
        creuserChemin(startX, startY, visite);
        
        // Définir Source et Terminaux
        definirTypesTuiles(startX, startY);
    }

    private void creuserChemin(int x, int y, boolean[][] visite) {
        visite[x][y] = true;
        
        // Directions aléatoires
        List<Integer> dirs = new ArrayList<>();
        dirs.add(Tuile.NORD); dirs.add(Tuile.SUD);
        dirs.add(Tuile.EST);  dirs.add(Tuile.OUEST);
        Collections.shuffle(dirs);

        for (int dir : dirs) {
            int dx = 0, dy = 0, oppose = 0;
            if (dir == Tuile.NORD) { dx = -1; oppose = Tuile.SUD; }
            if (dir == Tuile.SUD)  { dx = 1;  oppose = Tuile.NORD; }
            if (dir == Tuile.EST)  { dy = 1;  oppose = Tuile.OUEST; }
            if (dir == Tuile.OUEST){ dy = -1; oppose = Tuile.EST; }

            int nx = x + dx, ny = y + dy;

            if (nx >= 0 && nx < lignes && ny >= 0 && ny < colonnes && !visite[nx][ny]) {
                grille[x][y].ajouterConnexion(dir);
                grille[nx][ny].ajouterConnexion(oppose);
                creuserChemin(nx, ny, visite);
            }
        }
    }

    private void definirTypesTuiles(int sx, int sy) {
        // La case de départ est la SOURCE
        grille[sx][sy].setType(Type.SOURCE);
        grille[sx][sy].setConnectee(true);

        // Les bouts de tuyaux (1 seule connexion) deviennent des TERMINAUX
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                Tuile t = grille[i][j];
                // Compte les bits à 1 (astuce pour savoir le nombre de connexions)
                int nb = Integer.bitCount(t.getConnexions());
                if (nb == 1 && t.getType() != Type.SOURCE) {
                    t.setType(Type.TERMINAL);
                }
            }
        }
    }

    private void melangerTuiles() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                int rots = random.nextInt(4);
                for (int k = 0; k < rots; k++) grille[i][j].fairePivoter();
            }
        }
        marquerConnexions();
    }

    public void faireTournerTuile(int x, int y) {
        if (x >= 0 && x < lignes && y >= 0 && y < colonnes) {
            grille[x][y].fairePivoter();
            nombreDeCoups++;
            marquerConnexions();
        }
    }

    public void marquerConnexions() {
        // Reset sauf source
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if(grille[i][j].getType() != Type.SOURCE) grille[i][j].setConnectee(false);
            }
        }
        // Propagation
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (grille[i][j].getType() == Type.SOURCE) explorer(i, j);
            }
        }
    }

    private void explorer(int x, int y) {
        if (!grille[x][y].getEstConnectee()) return; // Sécurité

        int[] dirs = {Tuile.NORD, Tuile.SUD, Tuile.EST, Tuile.OUEST};
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for(int k=0; k<4; k++) {
            int nx = x + dx[k];
            int ny = y + dy[k];
            if(nx >= 0 && nx < lignes && ny >= 0 && ny < colonnes) {
                // Si lien valide et voisin pas encore allumé
                if(verifierLien(x, y, nx, ny) && !grille[nx][ny].getEstConnectee()) {
                    grille[nx][ny].setConnectee(true);
                    explorer(nx, ny);
                }
            }
        }
    }

    private boolean verifierLien(int x1, int y1, int x2, int y2) {
        Tuile t1 = grille[x1][y1];
        Tuile t2 = grille[x2][y2];
        int dir = 0;
        if (x2 == x1 - 1) dir = Tuile.NORD;
        else if (x2 == x1 + 1) dir = Tuile.SUD;
        else if (y2 == y1 + 1) dir = Tuile.EST;
        else if (y2 == y1 - 1) dir = Tuile.OUEST;
        
        return t1.aConnexion(dir) && t2.aConnexion(oppose(dir));
    }

    private int oppose(int dir) {
        if (dir == Tuile.NORD) return Tuile.SUD;
        if (dir == Tuile.SUD) return Tuile.NORD;
        if (dir == Tuile.EST) return Tuile.OUEST;
        return Tuile.EST;
    }

    public boolean partieTerminee() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (grille[i][j].getType() == Type.TERMINAL && !grille[i][j].getEstConnectee()) return false;
            }
        }
        return true;
    }

    public Tuile[][] getGrille() { return grille; }
    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }
    public int getNombreDeCoups() { return nombreDeCoups; }
}