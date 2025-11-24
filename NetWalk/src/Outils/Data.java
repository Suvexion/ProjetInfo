/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Outils;

import java.util.Random;

/**
 *
 * @author nicod
 */
public class Data {

    final static public int[] direction = {1, 2, 4, 8};  // N E S O
    final static public int[] directionOpposee = {4, 8, 1, 2}; // S O N E
    final static public String[] asciiGraphic = {
        "   ",                // 0 
        " \u2575 ",           // 1  N
        " \u2576\u2500",      // 2  E
        " \u2514\u2500",      // 3  N-E
        " \u2577 ",           // 4  S
        " \u2502 ",           // 5  S-N
        " \u250C\u2500",      // 6  S-E
        " \u251C\u2500",      // 7  S-E-N
        "\u2500\u2574 ",      // 8  O
        "\u2500\u2518 ",      // 9  O-N
        "\u2500\u2500\u2500", // 10 O-E
        "\u2500\u2534\u2500", // 11 O-E-N
        "\u2500\u2510 ",      // 12 O-S
        "\u2500\u2524 ",      // 13 O-S-N
        "\u2500\u252C\u2500", // 14 O-S-E
        "\u2500\u253C\u2500", // 15 O-S-E-N
    };
    public static final String RESET =  "\u001B[0m";
    public static final String BLACK =  "\u001B[30m";
    public static final String RED =    "\u001B[31m";
    public static final String GREEN =  "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE =   "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN =   "\u001B[36m";
    public static final String WHITE =  "\u001B[37m";

    public static int dirOpp(int dir){ // dir = 1,2,4,8
        int opp;
        switch (dir) {
        case 1:
            opp = 4;
            break;
        case 2:
            opp = 8;
            break;
        case 4:
            opp = 1;
            break;
        default:
            opp = 2;
            break;
        }
        return opp;    
    }
    
    public static void afficheTousLesCodes(){
        for(int i = 0; i<16; i++) {           
            System.out.println("i = "+i+"  ["+asciiGraphic[i]+"]");
        }
    }    
    public static void afficheUnCodeAvecCouleur(int c, String color){          
            System.out.print(RESET+"c = "+c+"  ["+color+asciiGraphic[c]+RESET+"]");
    }
}
