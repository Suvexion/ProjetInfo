package netwalk;

import java.awt.*;
import javax.swing.*;

public class InterfaceGraphique extends JFrame {

    private Jeu jeu;
    private JButton[][] boutons;
    private JLabel labelInfo;
    private JPanel panneauPrincipal;
    
    // Sauvegarde des paramètres actuels pour le bouton "Recommencer"
    private int tailleActuelle = 5; 

    public InterfaceGraphique() {
        this.setTitle("NetWalk - Projet Info");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 700);
        
        // On démarre sur le Menu de sélection
        afficherMenu();
        
        this.setVisible(true);
    }

    /**
     * Affiche l'écran de sélection de difficulté.
     */
    private void afficherMenu() {
        // Nettoyage de la fenêtre
        this.getContentPane().removeAll();
        this.setLayout(new GridBagLayout()); // Centrage des éléments
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Bienvenue dans NetWalk");
        titre.setFont(new Font("Arial", Font.BOLD, 30));
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel sousTitre = new JLabel("Choisissez votre niveau :");
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 18));
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Boutons de niveaux
        JButton btnDebutant = creerBoutonNiveau("Débutant (5x5)", 5);
        JButton btnInter = creerBoutonNiveau("Intermédiaire (10x10)", 10);
        JButton btnExpert = creerBoutonNiveau("Expert (15x15)", 15); // 20x20 peut être très petit sur l'écran

        menuPanel.add(titre);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(sousTitre);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(btnDebutant);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(btnInter);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(btnExpert);

        this.add(menuPanel);
        this.revalidate();
        this.repaint();
    }

    private JButton creerBoutonNiveau(String texte, int taille) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> lancerPartie(taille));
        return btn;
    }

    /**
     * Lance une nouvelle partie avec la taille donnée.
     */
    private void lancerPartie(int taille) {
        this.tailleActuelle = taille;
        this.jeu = new Jeu(taille, taille); // Génération du niveau parfait + mélange
        
        // Nettoyage de la fenêtre pour afficher le jeu
        this.getContentPane().removeAll();
        this.setLayout(new BorderLayout());

        // 1. HEADER (Haut) : Score et bouton Retour Menu
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JButton btnMenu = new JButton("Menu");
        btnMenu.addActionListener(e -> afficherMenu());
        
        labelInfo = new JLabel("Coups : 0");
        labelInfo.setFont(new Font("SansSerif", Font.BOLD, 20));
        labelInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(btnMenu, BorderLayout.WEST);
        headerPanel.add(labelInfo, BorderLayout.CENTER);
        this.add(headerPanel, BorderLayout.NORTH);

        // 2. GRILLE (Centre)
        panneauPrincipal = new JPanel();
        panneauPrincipal.setLayout(new GridLayout(taille, taille));
        
        boutons = new JButton[taille][taille];
        
        // Calcul de la taille de police dynamique selon la taille de la grille
        int fontSize = (taille > 10) ? 20 : 40; 
        Font fontTuile = new Font("Monospaced", Font.BOLD, fontSize);

        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                JButton btn = new JButton();
                btn.setFont(fontTuile);
                btn.setFocusPainted(false);
                
                final int r = i;
                final int c = j;
                btn.addActionListener(e -> jouerCoup(r, c));

                boutons[i][j] = btn;
                panneauPrincipal.add(btn);
            }
        }
        this.add(panneauPrincipal, BorderLayout.CENTER);

        // 3. FOOTER (Bas) : Légende et Reset
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(Color.LIGHT_GRAY);

        // Bouton Reset
        JButton btnReset = new JButton("Recommencer ce niveau");
        btnReset.addActionListener(e -> lancerPartie(tailleActuelle)); // Relance le même niveau (nouvelle génération)
        
        // Légende
        footerPanel.add(creerLabelLegende("Source", Color.ORANGE));
        footerPanel.add(creerLabelLegende("Connecté", Color.CYAN));
        footerPanel.add(creerLabelLegende("Déconnecté", Color.LIGHT_GRAY));
        footerPanel.add(creerLabelLegende("Terminal (OFF)", Color.PINK));
        footerPanel.add(creerLabelLegende("Terminal (ON)", Color.GREEN));
        footerPanel.add(btnReset);

        this.add(footerPanel, BorderLayout.SOUTH);

        rafraichirVue();
        this.revalidate();
        this.repaint();
    }

    private JLabel creerLabelLegende(String texte, Color couleur) {
        JLabel lbl = new JLabel("  " + texte + "  ");
        lbl.setOpaque(true);
        lbl.setBackground(couleur);
        lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return lbl;
    }

    private void jouerCoup(int r, int c) {
        jeu.faireTournerTuile(r, c);
        rafraichirVue();
        
        if (jeu.partieTerminee()) {
            labelInfo.setText("VICTOIRE ! (" + jeu.getNombreDeCoups() + " coups)");
            labelInfo.setForeground(new Color(0, 128, 0));
            int choix = JOptionPane.showConfirmDialog(this, 
                "Félicitations ! Vous avez rétabli le réseau !\nVoulez-vous rejouer ?", 
                "Victoire", JOptionPane.YES_NO_OPTION);
                
            if (choix == JOptionPane.YES_OPTION) {
                afficherMenu();
            }
        }
    }

    private void rafraichirVue() {
        labelInfo.setText("Coups : " + jeu.getNombreDeCoups());
        Tuile[][] grille = jeu.getGrille();

        for (int i = 0; i < jeu.getLignes(); i++) {
            for (int j = 0; j < jeu.getColonnes(); j++) {
                Tuile t = grille[i][j];
                JButton btn = boutons[i][j];

                btn.setText(t.toString());

                if (t.getType() == Tuile.Type.SOURCE) {
                    btn.setBackground(Color.ORANGE);
                } else if (t.getType() == Tuile.Type.TERMINAL) {
                    btn.setBackground(t.getEstConnectee() ? Color.GREEN : Color.PINK);
                } else {
                    btn.setBackground(t.getEstConnectee() ? Color.CYAN : Color.LIGHT_GRAY);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        // Lancement via le thread graphique (EDT) pour éviter les bugs d'affichage
        SwingUtilities.invokeLater(() -> new InterfaceGraphique());
    }
}