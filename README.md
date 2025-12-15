# ProjetInfo









TUILE :





package netwalk;



public class Tuile {



&nbsp;   public enum Type { SOURCE, TERMINAL, TUYAU }



&nbsp;   private int codeCase;

&nbsp;   private Type type;

&nbsp;   private boolean estConnectee;



&nbsp;   public static final int NORD  = 8; 

&nbsp;   public static final int EST   = 4; 

&nbsp;   public static final int SUD   = 2; 

&nbsp;   public static final int OUEST = 1; 



&nbsp;   public Tuile(int code, Type type, boolean connectee) {

&nbsp;       this.codeCase = code;

&nbsp;       this.type = type;

&nbsp;       this.estConnectee = connectee;

&nbsp;   }



&nbsp;   // Ajoute une connexion (utile pour le générateur)

&nbsp;   public void ajouterConnexion(int dir) {

&nbsp;       this.codeCase |= dir;

&nbsp;   }



&nbsp;   public void fairePivoter() {

&nbsp;       if (this.type == Type.SOURCE) return;

&nbsp;       this.codeCase = this.codeCase \* 2;

&nbsp;       if (this.codeCase > 15) this.codeCase = (this.codeCase % 16) + 1;

&nbsp;   }



&nbsp;   public boolean aConnexion(int dir) {

&nbsp;       return (this.codeCase \& dir) != 0;

&nbsp;   }



&nbsp;   // Getters / Setters

&nbsp;   public Type getType() { return type; }

&nbsp;   public void setType(Type t) { this.type = t; }

&nbsp;   public int getConnexions() { return codeCase; }

&nbsp;   public boolean getEstConnectee() { return estConnectee; }

&nbsp;   public void setConnectee(boolean b) { this.estConnectee = b; }



&nbsp;   @Override

&nbsp;   public String toString() {

&nbsp;       if (codeCase == 0) return " ";

&nbsp;       // Cas simples (Bouts)

&nbsp;       if (codeCase == NORD) return "\\u2575";

&nbsp;       if (codeCase == SUD) return "\\u2577";

&nbsp;       if (codeCase == EST) return "\\u2576";

&nbsp;       if (codeCase == OUEST) return "\\u2574";

&nbsp;       

&nbsp;       // Lignes

&nbsp;       if (codeCase == (NORD|SUD)) return "\\u2502";

&nbsp;       if (codeCase == (EST|OUEST)) return "\\u2500";



&nbsp;       // Coudes

&nbsp;       if (codeCase == (NORD|EST)) return "\\u2514";

&nbsp;       if (codeCase == (EST|SUD)) return "\\u250C";

&nbsp;       if (codeCase == (SUD|OUEST)) return "\\u2510";

&nbsp;       if (codeCase == (OUEST|NORD)) return "\\u2518";



&nbsp;       // T

&nbsp;       if (codeCase == (NORD|EST|SUD)) return "\\u2524";

&nbsp;       if (codeCase == (EST|SUD|OUEST)) return "\\u252C"; // T vers bas (car Ouest+Est+Sud)

&nbsp;       if (codeCase == (SUD|OUEST|NORD)) return "\\u251C";

&nbsp;       if (codeCase == (OUEST|NORD|EST)) return "\\u2534"; // T vers haut



&nbsp;       // Croix

&nbsp;       if (codeCase == 15) return "\\u253C";



&nbsp;       return "?";

&nbsp;   }

}









JEU : 



package netwalk;



import java.util.ArrayList;

import java.util.Collections;

import java.util.List;

import java.util.Random;

import netwalk.Tuile.Type;



public class Jeu {



&nbsp;   private Tuile\[]\[] grille;

&nbsp;   private int lignes;

&nbsp;   private int colonnes;

&nbsp;   private int nombreDeCoups = 0;

&nbsp;   private Random random = new Random();



&nbsp;   public Jeu(int lignes, int colonnes) {

&nbsp;       this.lignes = lignes;

&nbsp;       this.colonnes = colonnes;

&nbsp;       this.grille = new Tuile\[lignes]\[colonnes];

&nbsp;       

&nbsp;       // 1. Générer un réseau parfait (Arbre couvrant)

&nbsp;       genererNiveau(); 

&nbsp;       

&nbsp;       // 2. Mélanger les pièces

&nbsp;       melangerTuiles();

&nbsp;   }



&nbsp;   private void genererNiveau() {

&nbsp;       // Remplir de cases vides

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               grille\[i]\[j] = new Tuile(0, Type.TUYAU, false);

&nbsp;           }

&nbsp;       }

&nbsp;       

&nbsp;       // Creuser le labyrinthe depuis le centre

&nbsp;       int startX = lignes / 2;

&nbsp;       int startY = colonnes / 2;

&nbsp;       boolean\[]\[] visite = new boolean\[lignes]\[colonnes];

&nbsp;       creuserChemin(startX, startY, visite);

&nbsp;       

&nbsp;       // Définir Source et Terminaux

&nbsp;       definirTypesTuiles(startX, startY);

&nbsp;   }



&nbsp;   private void creuserChemin(int x, int y, boolean\[]\[] visite) {

&nbsp;       visite\[x]\[y] = true;

&nbsp;       

&nbsp;       // Directions aléatoires

&nbsp;       List<Integer> dirs = new ArrayList<>();

&nbsp;       dirs.add(Tuile.NORD); dirs.add(Tuile.SUD);

&nbsp;       dirs.add(Tuile.EST);  dirs.add(Tuile.OUEST);

&nbsp;       Collections.shuffle(dirs);



&nbsp;       for (int dir : dirs) {

&nbsp;           int dx = 0, dy = 0, oppose = 0;

&nbsp;           if (dir == Tuile.NORD) { dx = -1; oppose = Tuile.SUD; }

&nbsp;           if (dir == Tuile.SUD)  { dx = 1;  oppose = Tuile.NORD; }

&nbsp;           if (dir == Tuile.EST)  { dy = 1;  oppose = Tuile.OUEST; }

&nbsp;           if (dir == Tuile.OUEST){ dy = -1; oppose = Tuile.EST; }



&nbsp;           int nx = x + dx, ny = y + dy;



&nbsp;           if (nx >= 0 \&\& nx < lignes \&\& ny >= 0 \&\& ny < colonnes \&\& !visite\[nx]\[ny]) {

&nbsp;               grille\[x]\[y].ajouterConnexion(dir);

&nbsp;               grille\[nx]\[ny].ajouterConnexion(oppose);

&nbsp;               creuserChemin(nx, ny, visite);

&nbsp;           }

&nbsp;       }

&nbsp;   }



&nbsp;   private void definirTypesTuiles(int sx, int sy) {

&nbsp;       // La case de départ est la SOURCE

&nbsp;       grille\[sx]\[sy].setType(Type.SOURCE);

&nbsp;       grille\[sx]\[sy].setConnectee(true);



&nbsp;       // Les bouts de tuyaux (1 seule connexion) deviennent des TERMINAUX

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               Tuile t = grille\[i]\[j];

&nbsp;               // Compte les bits à 1 (astuce pour savoir le nombre de connexions)

&nbsp;               int nb = Integer.bitCount(t.getConnexions());

&nbsp;               if (nb == 1 \&\& t.getType() != Type.SOURCE) {

&nbsp;                   t.setType(Type.TERMINAL);

&nbsp;               }

&nbsp;           }

&nbsp;       }

&nbsp;   }



&nbsp;   private void melangerTuiles() {

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               int rots = random.nextInt(4);

&nbsp;               for (int k = 0; k < rots; k++) grille\[i]\[j].fairePivoter();

&nbsp;           }

&nbsp;       }

&nbsp;       marquerConnexions();

&nbsp;   }



&nbsp;   public void faireTournerTuile(int x, int y) {

&nbsp;       if (x >= 0 \&\& x < lignes \&\& y >= 0 \&\& y < colonnes) {

&nbsp;           grille\[x]\[y].fairePivoter();

&nbsp;           nombreDeCoups++;

&nbsp;           marquerConnexions();

&nbsp;       }

&nbsp;   }



&nbsp;   public void marquerConnexions() {

&nbsp;       // Reset sauf source

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               if(grille\[i]\[j].getType() != Type.SOURCE) grille\[i]\[j].setConnectee(false);

&nbsp;           }

&nbsp;       }

&nbsp;       // Propagation

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               if (grille\[i]\[j].getType() == Type.SOURCE) explorer(i, j);

&nbsp;           }

&nbsp;       }

&nbsp;   }



&nbsp;   private void explorer(int x, int y) {

&nbsp;       if (!grille\[x]\[y].getEstConnectee()) return; // Sécurité



&nbsp;       int\[] dirs = {Tuile.NORD, Tuile.SUD, Tuile.EST, Tuile.OUEST};

&nbsp;       int\[] dx = {-1, 1, 0, 0};

&nbsp;       int\[] dy = {0, 0, 1, -1};



&nbsp;       for(int k=0; k<4; k++) {

&nbsp;           int nx = x + dx\[k];

&nbsp;           int ny = y + dy\[k];

&nbsp;           if(nx >= 0 \&\& nx < lignes \&\& ny >= 0 \&\& ny < colonnes) {

&nbsp;               // Si lien valide et voisin pas encore allumé

&nbsp;               if(verifierLien(x, y, nx, ny) \&\& !grille\[nx]\[ny].getEstConnectee()) {

&nbsp;                   grille\[nx]\[ny].setConnectee(true);

&nbsp;                   explorer(nx, ny);

&nbsp;               }

&nbsp;           }

&nbsp;       }

&nbsp;   }



&nbsp;   private boolean verifierLien(int x1, int y1, int x2, int y2) {

&nbsp;       Tuile t1 = grille\[x1]\[y1];

&nbsp;       Tuile t2 = grille\[x2]\[y2];

&nbsp;       int dir = 0;

&nbsp;       if (x2 == x1 - 1) dir = Tuile.NORD;

&nbsp;       else if (x2 == x1 + 1) dir = Tuile.SUD;

&nbsp;       else if (y2 == y1 + 1) dir = Tuile.EST;

&nbsp;       else if (y2 == y1 - 1) dir = Tuile.OUEST;

&nbsp;       

&nbsp;       return t1.aConnexion(dir) \&\& t2.aConnexion(oppose(dir));

&nbsp;   }



&nbsp;   private int oppose(int dir) {

&nbsp;       if (dir == Tuile.NORD) return Tuile.SUD;

&nbsp;       if (dir == Tuile.SUD) return Tuile.NORD;

&nbsp;       if (dir == Tuile.EST) return Tuile.OUEST;

&nbsp;       return Tuile.EST;

&nbsp;   }



&nbsp;   public boolean partieTerminee() {

&nbsp;       for (int i = 0; i < lignes; i++) {

&nbsp;           for (int j = 0; j < colonnes; j++) {

&nbsp;               if (grille\[i]\[j].getType() == Type.TERMINAL \&\& !grille\[i]\[j].getEstConnectee()) return false;

&nbsp;           }

&nbsp;       }

&nbsp;       return true;

&nbsp;   }



&nbsp;   public Tuile\[]\[] getGrille() { return grille; }

&nbsp;   public int getLignes() { return lignes; }

&nbsp;   public int getColonnes() { return colonnes; }

&nbsp;   public int getNombreDeCoups() { return nombreDeCoups; }

}











INTERFACE GRAPHIQUE : 





package netwalk;



import java.awt.\*;

import javax.swing.\*;



public class InterfaceGraphique extends JFrame {



&nbsp;   private Jeu jeu;

&nbsp;   private JButton\[]\[] boutons;

&nbsp;   private JLabel labelInfo;

&nbsp;   private JPanel panneauPrincipal;

&nbsp;   

&nbsp;   // Sauvegarde des paramètres actuels pour le bouton "Recommencer"

&nbsp;   private int tailleActuelle = 5; 



&nbsp;   public InterfaceGraphique() {

&nbsp;       this.setTitle("NetWalk - Projet Info");

&nbsp;       this.setDefaultCloseOperation(JFrame.EXIT\_ON\_CLOSE);

&nbsp;       this.setSize(900, 700);

&nbsp;       

&nbsp;       // On démarre sur le Menu de sélection

&nbsp;       afficherMenu();

&nbsp;       

&nbsp;       this.setVisible(true);

&nbsp;   }



&nbsp;   /\*\*

&nbsp;    \* Affiche l'écran de sélection de difficulté.

&nbsp;    \*/

&nbsp;   private void afficherMenu() {

&nbsp;       // Nettoyage de la fenêtre

&nbsp;       this.getContentPane().removeAll();

&nbsp;       this.setLayout(new GridBagLayout()); // Centrage des éléments

&nbsp;       

&nbsp;       JPanel menuPanel = new JPanel();

&nbsp;       menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y\_AXIS));

&nbsp;       menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));



&nbsp;       JLabel titre = new JLabel("Bienvenue dans NetWalk");

&nbsp;       titre.setFont(new Font("Arial", Font.BOLD, 30));

&nbsp;       titre.setAlignmentX(Component.CENTER\_ALIGNMENT);

&nbsp;       

&nbsp;       JLabel sousTitre = new JLabel("Choisissez votre niveau :");

&nbsp;       sousTitre.setFont(new Font("Arial", Font.PLAIN, 18));

&nbsp;       sousTitre.setAlignmentX(Component.CENTER\_ALIGNMENT);



&nbsp;       // Boutons de niveaux

&nbsp;       JButton btnDebutant = creerBoutonNiveau("Débutant (5x5)", 5);

&nbsp;       JButton btnInter = creerBoutonNiveau("Intermédiaire (10x10)", 10);

&nbsp;       JButton btnExpert = creerBoutonNiveau("Expert (15x15)", 15); // 20x20 peut être très petit sur l'écran



&nbsp;       menuPanel.add(titre);

&nbsp;       menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

&nbsp;       menuPanel.add(sousTitre);

&nbsp;       menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

&nbsp;       menuPanel.add(btnDebutant);

&nbsp;       menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

&nbsp;       menuPanel.add(btnInter);

&nbsp;       menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

&nbsp;       menuPanel.add(btnExpert);



&nbsp;       this.add(menuPanel);

&nbsp;       this.revalidate();

&nbsp;       this.repaint();

&nbsp;   }



&nbsp;   private JButton creerBoutonNiveau(String texte, int taille) {

&nbsp;       JButton btn = new JButton(texte);

&nbsp;       btn.setFont(new Font("Arial", Font.BOLD, 16));

&nbsp;       btn.setAlignmentX(Component.CENTER\_ALIGNMENT);

&nbsp;       btn.addActionListener(e -> lancerPartie(taille));

&nbsp;       return btn;

&nbsp;   }



&nbsp;   /\*\*

&nbsp;    \* Lance une nouvelle partie avec la taille donnée.

&nbsp;    \*/

&nbsp;   private void lancerPartie(int taille) {

&nbsp;       this.tailleActuelle = taille;

&nbsp;       this.jeu = new Jeu(taille, taille); // Génération du niveau parfait + mélange

&nbsp;       

&nbsp;       // Nettoyage de la fenêtre pour afficher le jeu

&nbsp;       this.getContentPane().removeAll();

&nbsp;       this.setLayout(new BorderLayout());



&nbsp;       // 1. HEADER (Haut) : Score et bouton Retour Menu

&nbsp;       JPanel headerPanel = new JPanel(new BorderLayout());

&nbsp;       

&nbsp;       JButton btnMenu = new JButton("Menu");

&nbsp;       btnMenu.addActionListener(e -> afficherMenu());

&nbsp;       

&nbsp;       labelInfo = new JLabel("Coups : 0");

&nbsp;       labelInfo.setFont(new Font("SansSerif", Font.BOLD, 20));

&nbsp;       labelInfo.setHorizontalAlignment(SwingConstants.CENTER);

&nbsp;       

&nbsp;       headerPanel.add(btnMenu, BorderLayout.WEST);

&nbsp;       headerPanel.add(labelInfo, BorderLayout.CENTER);

&nbsp;       this.add(headerPanel, BorderLayout.NORTH);



&nbsp;       // 2. GRILLE (Centre)

&nbsp;       panneauPrincipal = new JPanel();

&nbsp;       panneauPrincipal.setLayout(new GridLayout(taille, taille));

&nbsp;       

&nbsp;       boutons = new JButton\[taille]\[taille];

&nbsp;       

&nbsp;       // Calcul de la taille de police dynamique selon la taille de la grille

&nbsp;       int fontSize = (taille > 10) ? 20 : 40; 

&nbsp;       Font fontTuile = new Font("Monospaced", Font.BOLD, fontSize);



&nbsp;       for (int i = 0; i < taille; i++) {

&nbsp;           for (int j = 0; j < taille; j++) {

&nbsp;               JButton btn = new JButton();

&nbsp;               btn.setFont(fontTuile);

&nbsp;               btn.setFocusPainted(false);

&nbsp;               

&nbsp;               final int r = i;

&nbsp;               final int c = j;

&nbsp;               btn.addActionListener(e -> jouerCoup(r, c));



&nbsp;               boutons\[i]\[j] = btn;

&nbsp;               panneauPrincipal.add(btn);

&nbsp;           }

&nbsp;       }

&nbsp;       this.add(panneauPrincipal, BorderLayout.CENTER);



&nbsp;       // 3. FOOTER (Bas) : Légende et Reset

&nbsp;       JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

&nbsp;       footerPanel.setBackground(Color.LIGHT\_GRAY);



&nbsp;       // Bouton Reset

&nbsp;       JButton btnReset = new JButton("Recommencer ce niveau");

&nbsp;       btnReset.addActionListener(e -> lancerPartie(tailleActuelle)); // Relance le même niveau (nouvelle génération)

&nbsp;       

&nbsp;       // Légende

&nbsp;       footerPanel.add(creerLabelLegende("Source", Color.ORANGE));

&nbsp;       footerPanel.add(creerLabelLegende("Connecté", Color.CYAN));

&nbsp;       footerPanel.add(creerLabelLegende("Déconnecté", Color.LIGHT\_GRAY));

&nbsp;       footerPanel.add(creerLabelLegende("Terminal (OFF)", Color.PINK));

&nbsp;       footerPanel.add(creerLabelLegende("Terminal (ON)", Color.GREEN));

&nbsp;       footerPanel.add(btnReset);



&nbsp;       this.add(footerPanel, BorderLayout.SOUTH);



&nbsp;       rafraichirVue();

&nbsp;       this.revalidate();

&nbsp;       this.repaint();

&nbsp;   }



&nbsp;   private JLabel creerLabelLegende(String texte, Color couleur) {

&nbsp;       JLabel lbl = new JLabel("  " + texte + "  ");

&nbsp;       lbl.setOpaque(true);

&nbsp;       lbl.setBackground(couleur);

&nbsp;       lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));

&nbsp;       return lbl;

&nbsp;   }



&nbsp;   private void jouerCoup(int r, int c) {

&nbsp;       jeu.faireTournerTuile(r, c);

&nbsp;       rafraichirVue();

&nbsp;       

&nbsp;       if (jeu.partieTerminee()) {

&nbsp;           labelInfo.setText("VICTOIRE ! (" + jeu.getNombreDeCoups() + " coups)");

&nbsp;           labelInfo.setForeground(new Color(0, 128, 0));

&nbsp;           int choix = JOptionPane.showConfirmDialog(this, 

&nbsp;               "Félicitations ! Vous avez rétabli le réseau !\\nVoulez-vous rejouer ?", 

&nbsp;               "Victoire", JOptionPane.YES\_NO\_OPTION);

&nbsp;               

&nbsp;           if (choix == JOptionPane.YES\_OPTION) {

&nbsp;               afficherMenu();

&nbsp;           }

&nbsp;       }

&nbsp;   }



&nbsp;   private void rafraichirVue() {

&nbsp;       labelInfo.setText("Coups : " + jeu.getNombreDeCoups());

&nbsp;       Tuile\[]\[] grille = jeu.getGrille();



&nbsp;       for (int i = 0; i < jeu.getLignes(); i++) {

&nbsp;           for (int j = 0; j < jeu.getColonnes(); j++) {

&nbsp;               Tuile t = grille\[i]\[j];

&nbsp;               JButton btn = boutons\[i]\[j];



&nbsp;               btn.setText(t.toString());



&nbsp;               if (t.getType() == Tuile.Type.SOURCE) {

&nbsp;                   btn.setBackground(Color.ORANGE);

&nbsp;               } else if (t.getType() == Tuile.Type.TERMINAL) {

&nbsp;                   btn.setBackground(t.getEstConnectee() ? Color.GREEN : Color.PINK);

&nbsp;               } else {

&nbsp;                   btn.setBackground(t.getEstConnectee() ? Color.CYAN : Color.LIGHT\_GRAY);

&nbsp;               }

&nbsp;           }

&nbsp;       }

&nbsp;   }

&nbsp;   

&nbsp;   public static void main(String\[] args) {

&nbsp;       // Lancement via le thread graphique (EDT) pour éviter les bugs d'affichage

&nbsp;       SwingUtilities.invokeLater(() -> new InterfaceGraphique());

&nbsp;   }

}

