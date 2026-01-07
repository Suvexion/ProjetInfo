package netwalk;

import java.util.*;
import netwalk.Tuile.Type;

public class Jeu {
    private Tuile[][] grille;
    private int lignes, colonnes, nombreDeCoups = 0;
    private long seed;
    private Random random;

    public Jeu(int lignes, int colonnes, long seed) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.seed = seed;
        this.random = new Random(seed);
        this.grille = new Tuile[lignes][colonnes];
        genererNiveau(); 
        melangerTuiles();
    }

    private void genererNiveau() {
        for (int i = 0; i < lignes; i++)
            for (int j = 0; j < colonnes; j++)
                grille[i][j] = new Tuile(0, Type.TUYAU, false);
        
        boolean[][] visite = new boolean[lignes][colonnes];
        creuserChemin(lignes/2, colonnes/2, visite);
        definirTypesTuiles(lignes/2, colonnes/2);
    }

    private void creuserChemin(int x, int y, boolean[][] visite) {
        visite[x][y] = true;
        List<Integer> dirs = Arrays.asList(Tuile.NORD, Tuile.SUD, Tuile.EST, Tuile.OUEST);
        Collections.shuffle(dirs, random);

        for (int dir : dirs) {
            int dx = (dir == Tuile.NORD) ? -1 : (dir == Tuile.SUD) ? 1 : 0;
            int dy = (dir == Tuile.EST) ? 1 : (dir == Tuile.OUEST) ? -1 : 0;
            int nx = x + dx, ny = y + dy;

            if (nx >= 0 && nx < lignes && ny >= 0 && ny < colonnes && !visite[nx][ny]) {
                grille[x][y].ajouterConnexion(dir);
                grille[nx][ny].ajouterConnexion(oppose(dir));
                creuserChemin(nx, ny, visite);
            }
        }
    }

    private void definirTypesTuiles(int sx, int sy) {
        grille[sx][sy].setType(Type.SOURCE);
        grille[sx][sy].setConnectee(true);
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (Integer.bitCount(grille[i][j].getConnexions()) == 1 && grille[i][j].getType() != Type.SOURCE)
                    grille[i][j].setType(Type.TERMINAL);
            }
        }
    }

    private void melangerTuiles() {
        for (Tuile[] row : grille)
            for (Tuile t : row) {
                int r = random.nextInt(4);
                for (int i = 0; i < r; i++) t.fairePivoter();
            }
        marquerConnexions();
    }

    public void faireTournerTuile(int x, int y) {
        grille[x][y].fairePivoter();
        nombreDeCoups++;
        marquerConnexions();
    }

    public void marquerConnexions() {
        for (Tuile[] row : grille)
            for (Tuile t : row) if(t.getType() != Type.SOURCE) t.setConnectee(false);
        propager(lignes/2, colonnes/2);
    }

    private void propager(int x, int y) {
        int[] dx = {-1, 1, 0, 0}, dy = {0, 0, 1, -1};
        for(int k=0; k<4; k++) {
            int nx = x + dx[k], ny = y + dy[k];
            if(nx >= 0 && nx < lignes && ny >= 0 && ny < colonnes && !grille[nx][ny].getEstConnectee()) {
                if(verifierLien(x, y, nx, ny)) {
                    grille[nx][ny].setConnectee(true);
                    propager(nx, ny);
                }
            }
        }
    }

    private boolean verifierLien(int x1, int y1, int x2, int y2) {
        Tuile t1 = grille[x1][y1];
        Tuile t2 = grille[x2][y2];
        int dirT1VersT2 = 0;
        if (x2 == x1 - 1) dirT1VersT2 = Tuile.NORD;
        else if (x2 == x1 + 1) dirT1VersT2 = Tuile.SUD;
        else if (y2 == y1 + 1) dirT1VersT2 = Tuile.EST;
        else if (y2 == y1 - 1) dirT1VersT2 = Tuile.OUEST;
        if (dirT1VersT2 == 0) return false;
        int dirT2VersT1 = oppose(dirT1VersT2);
        return t1.aConnexion(dirT1VersT2) && t2.aConnexion(dirT2VersT1);
    }

    private int oppose(int d) { return (d==8)?2:(d==2)?8:(d==4)?1:4; }

    public boolean partieTerminee() {
        for(Tuile[] r : grille) for(Tuile t : r) if(t.getType()==Type.TERMINAL && !t.getEstConnectee()) return false;
        return true;
    }

    public Tuile[][] getGrille() { return grille; }
    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }
    public int getNombreDeCoups() { return nombreDeCoups; }
}