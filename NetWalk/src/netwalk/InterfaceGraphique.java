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