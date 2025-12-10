/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package netwalk;

/**
 *
 * @author gdarre
 */
package netwalk; 

public class TestNEtWalk {

    // Constantes pour référence (reprises de Tuile.java)
    public static final int NORD  = 1; 
    public static final int EST   = 2; 
    public static final int SUD   = 4; 
    public static final int OUEST = 8;
    public static final int LIMITE_MAX = 15;

    public static void main(String[] args) {
        System.out.println("--- Démarrage des Tests Unitaires NetWalk ---");

        // Test 1 : Vérification de la Rotation Simple
        testRotation(NORD, EST, "Rotation NORD -> EST");
        testRotation(OUEST, NORD, "Rotation OUEST -> NORD (Wrap)");
        
        // Test 2 : Vérification de la Rotation d'un Coude
        // Coude NORD+OUEST (1 + 8 = 9) doit devenir EST+NORD (2 + 1 = 3) après 3 tours
        testRotationComplete(9, 3);

        // Test 3 : Vérification de la Connexion
        testConnexion();
        
        System.out.println("\n--- Fin des Tests ---");
    }

    /**
     * Vérifie qu'une seule rotation déplace la connexion de 'valeurInitiale' à 'valeurAttendue'.
     */
    public static void testRotation(int valeurInitiale, int valeurAttendue, String description) {
        Tuile tuileTest = new Tuile(valeurInitiale, "TUYAU", false);
        tuileTest.tourner();

        // Récupère la valeur après rotation
        int resultat = tuileTest.getConnexions(); 

        System.out.print("\n[TEST ROTATION] " + description + " : ");
        if (resultat == valeurAttendue) {
            System.out.println("SUCCÈS");
        } else {
            System.err.println("ÉCHEC. Attendu: " + valeurAttendue + ", Obtenu: " + resultat);
        }
    }
    
    /**
     * Vérifie une forme de tuile complexe après plusieurs rotations.
     */
    public static void testRotationComplete(int valeurInitiale, int valeurAttendue) {
        Tuile tuileTest = new Tuile(valeurInitiale, "TUYAU", false);
        // On tourne 3 fois
        tuileTest.tourner();
        tuileTest.tourner();
        tuileTest.tourner();
        
        int resultat = tuileTest.getConnexions();
        System.out.print("\n[TEST ROTATION COMPLEXE] 3 tours : ");
        
        if (resultat == valeurAttendue) {
            System.out.println("SUCCÈS");
        } else {
            System.err.println("ÉCHEC. Attendu: " + valeurAttendue + ", Obtenu: " + resultat);
        }
    }

    /**
     * Vérifie la logique de connexion binaire.
     */
    public static void testConnexion() {
        // Tuile T connectée Nord et Sud (1 + 4 = 5)
        Tuile tuileT = new Tuile(NORD | SUD, "TUYAU", false); 
        
        System.out.print("\n[TEST CONNEXION] Connexion NORD : ");
        if (tuileT.aConnexion(NORD)) {
             System.out.println("SUCCÈS");
        } else {
             System.err.println("ÉCHEC");
        }
        
        System.out.print("[TEST CONNEXION] Connexion EST (Absente) : ");
        if (!tuileT.aConnexion(EST)) {
             System.out.println("SUCCÈS");
        } else {
             System.err.println("ÉCHEC");
        }
    }
}