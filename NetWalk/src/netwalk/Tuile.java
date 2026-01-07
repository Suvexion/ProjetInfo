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