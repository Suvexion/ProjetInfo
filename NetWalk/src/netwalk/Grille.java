package netwalk;
import java.util.Random;

public class Grille {
    private Tuile[][] matrice;
    private int lignes;
    private int colonnes;
    private Random random = new Random();

    public Grille(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.matrice = new Tuile[lignes][colonnes];
        initialiserGrille();
    }

    private void initialiserGrille() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                matrice[i][j] = new Tuile(random.nextInt(15) + 1);
            }
        }
        // Configuration fixe pour l'exemple (Source à Gauche, Terminal à Droite)
        Tuile source = matrice[1][0];
        source.setSource(true);
        source.setCode(4); // Forme ╶ (Est)
        
        Tuile terminal = matrice[1][colonnes-1];
        terminal.setTerminal(true);
        
        updateConnexions(); 
    }

    public void actionJoueur(int lig, int col, String sens) {
        if (lig < 0 || lig >= lignes || col < 0 || col >= colonnes) return;
        matrice[lig][col].tourner(sens.equalsIgnoreCase("d"));
        updateConnexions(); 
    }
    
    private void updateConnexions() {
        // Reset
        for (int i = 0; i < lignes; i++) 
            for (int j = 0; j < colonnes; j++) 
                if (!matrice[i][j].estSource()) matrice[i][j].setConnectee(false);
        
        // Propagation
        for (int i = 0; i < lignes; i++) 
            for (int j = 0; j < colonnes; j++) 
                if (matrice[i][j].estSource()) propager(i, j);
    }

    private void propager(int l, int c) {
        int[] dL = {-1, 1, 0, 0};
        int[] dC = {0, 0, 1, -1};
        int[] dirs = {8, 2, 4, 1}; // N, S, E, O
        int[] opps = {2, 8, 1, 4}; // S, N, O, E

        for (int k = 0; k < 4; k++) {
            int nL = l + dL[k];
            int nC = c + dC[k];
            if (nL >= 0 && nL < lignes && nC >= 0 && nC < colonnes) {
                if (!matrice[nL][nC].estConnectee()) {
                    if (matrice[l][c].aConnexion(dirs[k]) && matrice[nL][nC].aConnexion(opps[k])) {
                        matrice[nL][nC].setConnectee(true);
                        propager(nL, nC);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Indices Colonnes
        sb.append("    ");
        for (int j = 0; j < colonnes; j++) sb.append(" ").append(j).append(" ");
        sb.append("\n");

        // Cadre Haut Continu (┌───┐)
        sb.append("    \u250C"); 
        for (int j = 0; j < colonnes; j++) sb.append("\u2500\u2500\u2500"); 
        sb.append("\u2510\n");

        // Contenu
        for (int i = 0; i < lignes; i++) {
            sb.append(" ").append(i).append("  \u2502"); // Ligne verticale (│)
            for (int j = 0; j < colonnes; j++) {
                sb.append(" ").append(matrice[i][j]).append(" ");
            }
            sb.append("\u2502  ").append(i).append("\n"); // Ligne verticale (│)
        }

        // Cadre Bas Continu (└───┘)
        sb.append("    \u2514");
        for (int j = 0; j < colonnes; j++) sb.append("\u2500\u2500\u2500");
        sb.append("\u2518\n");
        
        return sb.toString();
    }
}