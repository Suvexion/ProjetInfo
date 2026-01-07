# ProjetInfo









TUILE :

package netwalk;

public class Tuile {
    // --- CONSTANTES DE DIRECTION (Pour ta classe Jeu) ---
    public static final int NORD = 8;
    public static final int EST = 4;
    public static final int SUD = 2;
    public static final int OUEST = 1;

    // --- ENUM TYPE (Pour ta classe Jeu) ---
    public enum Type { SOURCE, TERMINAL, TUYAU }

    // --- COULEURS ---
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";    
    public static final String CYAN = "\u001B[36m";   
    public static final String WHITE = "\u001B[37m";  
    public static final String GREEN = "\u001B[32m";  
    public static final String PURPLE = "\u001B[35m"; 

    private int code; 
    private Type type;
    private boolean estConnectee;

    // Constructeur compatible avec ta classe Jeu
    public Tuile(int code, Type type, boolean connectee) {
        this.code = code;
        this.type = type;
        this.estConnectee = connectee;
    }

    // --- MÉTHODES UTILISÉES PAR JEU ---
    public void ajouterConnexion(int dir) {
        this.code |= dir; // Ajoute le bit de direction
    }

    public int getConnexions() {
        return code;
    }

    public void fairePivoter() {
        if (type == Type.SOURCE) return; // La source est fixe
        // Rotation : N->E, E->S, S->O, O->N
        int newCode = 0;
        if ((code & NORD) != 0) newCode += EST; 
        if ((code & EST) != 0) newCode += SUD; 
        if ((code & SUD) != 0) newCode += OUEST; 
        if ((code & OUEST) != 0) newCode += NORD; 
        this.code = newCode;
    }

    public boolean aConnexion(int dir) {
        return (code & dir) != 0;
    }

    // --- GETTERS / SETTERS ---
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public boolean getEstConnectee() { return estConnectee; }
    public void setConnectee(boolean c) { this.estConnectee = c; }
    

    // --- AFFICHAGE (Le rendu visuel) ---
    @Override
    public String toString() {
        String c = WHITE;
        if (type == Type.SOURCE) c = RED;
        else if (type == Type.TERMINAL) c = estConnectee ? GREEN : PURPLE;
        else if (estConnectee) c = CYAN;

        String s = switch (code) {
            case 0 -> " ";
            case 1 -> "\u2574"; // ╴ Ouest
            case 2 -> "\u2577"; // ╷ Sud
            case 3 -> "\u2510"; // ┐ 
            case 4 -> "\u2576"; // ╶ Est
            case 5 -> "\u2500"; // ─ 
            case 6 -> "\u250C"; // ┌ 
            case 7 -> "\u252C"; // ┬ 
            case 8 -> "\u2575"; // ╵ Nord
            case 9 -> "\u2518"; // ┘ 
            case 10 -> "\u2502"; // │ 
            case 11 -> "\u2524"; // ┤ 
            case 12 -> "\u2514"; // └ 
            case 13 -> "\u2534"; // ┴ 
            case 14 -> "\u251C"; // ├ 
            case 15 -> "\u253C"; // ┼ 
            default -> "?";
        };
        return c + s + RESET;
    }
}

JEU : 

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



TESTNETWALK : 

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



INTERFACE GRAPHIQUE : 

package netwalk;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.*;

public class InterfaceGraphique extends JFrame {
    private Jeu jeu;
    private JPanel grillePanel;
    private JLabel labelTemps;
    private JLabel labelCoups;
    private Timer timer;
    private long tempsDebut;
    private boolean partieFinie = false;

    // Configuration des couleurs (Style "Dark Mode" ou "Retro")
    private final Color COLOR_BG = new Color(40, 40, 40);       // Gris foncé
    private final Color COLOR_PIPE_OFF = new Color(100, 100, 100); // Gris (éteint)
    private final Color COLOR_PIPE_ON = new Color(0, 200, 255);    // Cyan (allumé)
    private final Color COLOR_SOURCE = new Color(255, 69, 0);      // Orange/Rouge
    private final Color COLOR_TERMINAL_OFF = new Color(128, 0, 128); // Violet
    private final Color COLOR_TERMINAL_ON = new Color(50, 205, 50);  // Vert

    public InterfaceGraphique() {
        setTitle("NetWalk - Projet Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setLayout(new BorderLayout());

        // 1. Initialisation du jeu
        lancerNouveauJeu();

        // 2. Panneau du Haut (Infos)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        topPanel.setBackground(new Color(30, 30, 30));
        
        labelCoups = new JLabel("Coups: 0");
        labelCoups.setForeground(Color.WHITE);
        labelCoups.setFont(new Font("Arial", Font.BOLD, 18));
        
        labelTemps = new JLabel("Temps: 00:00");
        labelTemps.setForeground(Color.WHITE);
        labelTemps.setFont(new Font("Monospaced", Font.BOLD, 18));

        topPanel.add(labelCoups);
        topPanel.add(labelTemps);
        add(topPanel, BorderLayout.NORTH);

        // 3. Panneau Central (Grille de Jeu)
        grillePanel = new JPanel();
        grillePanel.setBackground(COLOR_BG);
        add(grillePanel, BorderLayout.CENTER);

        // 4. Panneau du Bas (Boutons)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 30, 30));
        
        JButton btnRestart = new JButton("Recommencer");
        btnRestart.setFocusable(false);
        btnRestart.addActionListener(e -> lancerNouveauJeu());
        
        bottomPanel.add(btnRestart);
        add(bottomPanel, BorderLayout.SOUTH);

        // 5. Timer pour le chronomètre (1 seconde)
        timer = new Timer(1000, e -> mettreAJourChrono());
        timer.start();

        // Premier dessin
        dessinerGrille();
        setVisible(true);
    }

    private void lancerNouveauJeu() {
        // Création d'un jeu 5x7 (ou autre taille)
        this.jeu = new Jeu(5, 7, System.currentTimeMillis());
        this.tempsDebut = System.currentTimeMillis();
        this.partieFinie = false;
        
        if (timer != null) timer.restart();
        if (labelCoups != null) labelCoups.setText("Coups: 0");
        
        dessinerGrille();
    }

    private void mettreAJourChrono() {
        if (partieFinie) return;
        long millis = System.currentTimeMillis() - tempsDebut;
        long secondes = millis / 1000;
        long min = secondes / 60;
        long sec = secondes % 60;
        labelTemps.setText(String.format("Temps: %02d:%02d", min, sec));
    }

    // --- CŒUR DU DESSIN ---
    private void dessinerGrille() {
        if (grillePanel == null) return;
        
        grillePanel.removeAll();
        int rows = jeu.getLignes();
        int cols = jeu.getColonnes();
        grillePanel.setLayout(new GridLayout(rows, cols));

        Tuile[][] grille = jeu.getGrille();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final int r = i;
                final int c = j;
                
                // On crée un composant personnalisé pour chaque tuile
                PanneauTuile pTuile = new PanneauTuile(grille[i][j]);
                
                // GESTION DES CLICS SOURIS
                pTuile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (partieFinie) return;

                        if (SwingUtilities.isRightMouseButton(e)) {
                            // Clic Droit -> Rotation horaire (normale)
                            jeu.faireTournerTuile(r, c);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            // Clic Gauche -> Rotation anti-horaire
                            // (Astuce : 3 rotations à droite = 1 à gauche)
                            jeu.faireTournerTuile(r, c);
                            jeu.faireTournerTuile(r, c);
                            jeu.faireTournerTuile(r, c); 
                            // Note: Le compteur de coups augmentera de 3, 
                            // si tu veux éviter ça, il faudrait une méthode "tournerGauche" dans Jeu.
                        }
                        
                        verifierVictoire();
                        labelCoups.setText("Coups: " + jeu.getNombreDeCoups());
                        
                        // On redessine tout (bourrin mais sûr pour les connexions)
                        grillePanel.revalidate();
                        grillePanel.repaint();
                    }
                });
                
                grillePanel.add(pTuile);
            }
        }
        grillePanel.revalidate();
        grillePanel.repaint();
    }

    private void verifierVictoire() {
        if (jeu.partieTerminee()) {
            partieFinie = true;
            timer.stop();
            grillePanel.repaint(); // Pour afficher les dernières couleurs
            
            long secondes = (System.currentTimeMillis() - tempsDebut) / 1000;
            JOptionPane.showMessageDialog(this, 
                "FÉLICITATIONS !\n\n" +
                "Niveau terminé.\n" +
                "Coups : " + jeu.getNombreDeCoups() + "\n" +
                "Temps : " + secondes + "s",
                "Victoire", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- COMPOSANT VISUEL D'UNE TUILE ---
    // C'est ici qu'on dessine les lignes, les carrés, etc.
    private class PanneauTuile extends JPanel {
        private Tuile tuile;

        public PanneauTuile(Tuile t) {
            this.tuile = t;
            this.setBackground(COLOR_BG);
            this.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1)); // Légère bordure
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            // Anti-aliasing pour que ce soit joli (moins pixelisé)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;
            
            // Déterminer la couleur
            Color couleurLigne = tuile.getEstConnectee() ? COLOR_PIPE_ON : COLOR_PIPE_OFF;
            if (tuile.getType() == Tuile.Type.SOURCE) couleurLigne = COLOR_SOURCE;

            // Épaisseur du tuyau
            int epaisseur = Math.min(w, h) / 5; 
            g2.setStroke(new BasicStroke(epaisseur, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(couleurLigne);

            // DESSIN DES CONNEXIONS (Tuyaux)
            int code = tuile.getConnexions(); // Assure-toi que cette méthode existe dans Tuile (sinon getCode())
            
            // Nord (8)
            if ((code & 8) != 0) g2.draw(new Line2D.Float(cx, cy, cx, 0));
            // Sud (2)
            if ((code & 2) != 0) g2.draw(new Line2D.Float(cx, cy, cx, h));
            // Est (4)
            if ((code & 4) != 0) g2.draw(new Line2D.Float(cx, cy, w, cy));
            // Ouest (1)
            if ((code & 1) != 0) g2.draw(new Line2D.Float(cx, cy, 0, cy));

            // DESSIN DU CENTRE (Formes Spécifiques)
            int tailleCentre = epaisseur * 2;
            int xCentre = cx - tailleCentre / 2;
            int yCentre = cy - tailleCentre / 2;

            if (tuile.getType() == Tuile.Type.SOURCE) {
                // Source : Un carré plein Orange/Rouge
                g2.setColor(COLOR_SOURCE);
                g2.fillRect(xCentre, yCentre, tailleCentre, tailleCentre);
                // Petit contour blanc pour le style
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(xCentre, yCentre, tailleCentre, tailleCentre);

            } else if (tuile.getType() == Tuile.Type.TERMINAL) {
                // Terminal : Un carré avec contour, couleur change selon connexion
                g2.setColor(tuile.getEstConnectee() ? COLOR_TERMINAL_ON : COLOR_TERMINAL_OFF);
                g2.fillRect(xCentre, yCentre, tailleCentre, tailleCentre);
                
                // Dessin "Écran" (petit rectangle noir dedans)
                g2.setColor(Color.BLACK);
                g2.fillRect(xCentre + 4, yCentre + 4, tailleCentre - 8, tailleCentre - 8);
            } else {
                // Tuyau normal : un petit rond central pour lisser les jointures
                g2.setColor(couleurLigne);
                g2.fillOval(cx - epaisseur/2, cy - epaisseur/2, epaisseur, epaisseur);
            }
        }
    }

    public static void main(String[] args) {
        // Lancer l'interface dans le thread graphique
        SwingUtilities.invokeLater(InterfaceGraphique::new);
    }
}