package netwalk;

public class Tuile {

    public enum Type { SOURCE, TERMINAL, TUYAU }

    private int codeCase;
    private Type type;
    private boolean estConnectee;

    public static final int NORD  = 8; 
    public static final int EST   = 4; 
    public static final int SUD   = 2; 
    public static final int OUEST = 1; 

    public Tuile(int code, Type type, boolean connectee) {
        this.codeCase = code;
        this.type = type;
        this.estConnectee = connectee;
    }

    // Ajoute une connexion (utile pour le générateur)
    public void ajouterConnexion(int dir) {
        this.codeCase |= dir;
    }

    public void fairePivoter() {
        if (this.type == Type.SOURCE) return;
        this.codeCase = this.codeCase * 2;
        if (this.codeCase > 15) this.codeCase = (this.codeCase % 16) + 1;
    }

    public boolean aConnexion(int dir) {
        return (this.codeCase & dir) != 0;
    }

    // Getters / Setters
    public Type getType() { return type; }
    public void setType(Type t) { this.type = t; }
    public int getConnexions() { return codeCase; }
    public boolean getEstConnectee() { return estConnectee; }
    public void setConnectee(boolean b) { this.estConnectee = b; }

    @Override
    public String toString() {
        if (codeCase == 0) return " ";
        // Cas simples (Bouts)
        if (codeCase == NORD) return "\u2575";
        if (codeCase == SUD) return "\u2577";
        if (codeCase == EST) return "\u2576";
        if (codeCase == OUEST) return "\u2574";
        
        // Lignes
        if (codeCase == (NORD|SUD)) return "\u2502";
        if (codeCase == (EST|OUEST)) return "\u2500";

        // Coudes
        if (codeCase == (NORD|EST)) return "\u2514";
        if (codeCase == (EST|SUD)) return "\u250C";
        if (codeCase == (SUD|OUEST)) return "\u2510";
        if (codeCase == (OUEST|NORD)) return "\u2518";

        // T
        if (codeCase == (NORD|EST|SUD)) return "\u2524";
        if (codeCase == (EST|SUD|OUEST)) return "\u252C"; // T vers bas (car Ouest+Est+Sud)
        if (codeCase == (SUD|OUEST|NORD)) return "\u251C";
        if (codeCase == (OUEST|NORD|EST)) return "\u2534"; // T vers haut

        // Croix
        if (codeCase == 15) return "\u253C";

        return "?";
    }
}