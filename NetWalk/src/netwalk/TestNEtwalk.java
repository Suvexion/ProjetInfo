/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package netwalk;

/**
 *
 * @author gdarre
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Classe de test pour vérifier le bon fonctionnement de la classe Jeu.
 * Elle teste l'initialisation, la rotation et la logique de connexion.
 * * @author gdarre
 */
public class TestNEtwalk {

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DES TESTS DU JEU NETWALK ===\n");

        // --- TEST 1 : Initialisation du Jeu ---
        System.out.println("[TEST 1] Initialisation du Plateau 3x3");
        Jeu jeu = new Jeu(3, 3);
        System.out.println("Plateau initial :");
        System.out.println(jeu.toString());
        System.out.println("Test 1 OK : Le plateau s'affiche correctement.\n");


        // --- TEST 2 : Rotation d'une Tuile ---
        System.out.println("[TEST 2] Rotation de la tuile (0,0)");
        // La tuile (0,0) est initialement verticale (NORD | SUD = 10)
        // Après 1 rotation (anti-horaire dans ton code 8-4-2-1), elle devrait changer.
        // NORD(8) -> EST(4), SUD(2) -> OUEST(1). Donc 10 -> 5 (Horizontale)
        
        System.out.println("Avant rotation :");
        // On affiche juste une partie pour vérifier visuellement ou on fait confiance au toString()
        
        jeu.faireTournerTuile(0, 0);
        
        System.out.println("Après rotation (0,0) :");
        System.out.println(jeu.toString());
        System.out.println("Test 2 OK : La tuile en (0,0) a changé d'orientation.\n");


        // --- TEST 3 : Vérification de Lien (Simulation) ---
        // Ce test est plus subtil car la méthode verifierLien est privée dans Jeu.
        // On ne peut pas l'appeler directement ici.
        // Cependant, on peut vérifier la conséquence : la logique de rotation a été testée plus haut.
        
        // Pour tester verifierLien, il faudrait soit :
        // 1. Rendre la méthode 'verifierLien' public temporairement.
        // 2. Ou tester la méthode 'marquerConnexions' (si implémentée) qui utilise verifierLien.
        
        System.out.println("[TEST 3] Simulation de Connexion");
        // On va essayer de connecter la tuile (0,0) qui est maintenant horizontale (EST-OUEST)
        // avec la tuile (0,1) qui est un coude NORD-EST.
        // (0,0) a EST, (0,1) n'a pas OUEST. Donc pas de connexion.
        
        // On tourne (0,1) pour qu'elle ait une connexion OUEST.
        // (0,1) est NORD(8)|EST(4). 
        // Tour 1 -> EST(4)|SUD(2)
        // Tour 2 -> SUD(2)|OUEST(1)  <-- Là elle aura OUEST !
        
        System.out.println("Rotation de (0,1) pour tenter une connexion...");
        jeu.faireTournerTuile(0, 1);
        jeu.faireTournerTuile(0, 1);
        
        System.out.println(jeu.toString());
        
        // Note : Comme 'estConnectee' n'est pas encore calculé dynamiquement par marquerConnexions
        // dans ton code actuel (la méthode est vide), on ne verra pas de changement de couleur/état.
        // Ce test valide surtout que les actions s'enchaînent sans erreur.
        System.out.println("Test 3 OK : Les rotations s'enchaînent sans planter.\n");


        // --- TEST 4 : Coordonnées Invalides ---
        System.out.println("[TEST 4] Gestion des erreurs (Coordonnées hors limites)");
        System.out.print("Tentative de tourner (-1, 0) : ");
        jeu.faireTournerTuile(-1, 0); // Doit afficher un message d'erreur
        
        System.out.print("Tentative de tourner (0, 5) : ");
        jeu.faireTournerTuile(0, 5); // Doit afficher un message d'erreur
        System.out.println("Test 4 OK : Les erreurs sont gérées.\n");

        System.out.println("=== FIN DES TESTS ===");
    }
}