package com.cometproject.api.utilities;

public class ModelUtils {
    public static char heightToMap(int height){
        return heightToMap((short) height);
    }

    public static char heightToMap(short height){
        switch (height){
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            case 10:
                return 'a';
            case 11:
                return 'b';
            case 12:
                return 'c';
            case 13:
                return 'd';
            case 14:
                return 'd';
            case 15:
                return 'f';
            case 16:
                return 'g';
            case 17:
                return 'h';
            case 18:
                return 'i';
            case 19:
                return 'j';
            case 20:
                return 'k';
            case 21:
                return 'l';
            case 22:
                return 'm';
            case 23:
                return 'n';
            case 24:
                return 'o';
            case 25:
                return 'p';
            case 26:
                return 'q';
            case 27:
                return 'r';
            case 28:
                return 's';
            case 29:
                return 't';
            case 30:
                return 'u';
            case 31:
                return 'v';
            case 32:
                return 'w';
            case 33:
                return 'x';
            case 34:
                return 'y';
            case 35:
                return 'z';

            default:
                return '0';
        }
    }

    public static int getHeight(char c) {
        switch (String.valueOf(c)) {
            case "1":
                return 1;
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 4;
            case "5":
                return 5;
            case "6":
                return 6;
            case "7":
                return 7;
            case "8":
                return 8;
            case "9":
                return 9;
            case "a":
                return 10;
            case "b":
                return 11;
            case "c":
                return 12;
            case "d":
                return 13;
            case "e":
                return 14;
            case "f":
                return 15;
            case "g":
                return 16;
            case "h":
                return 17;
            case "i":
                return 18;
            case "j":
                return 19;
            case "k":
                return 20;
            case "l":
                return 21;
            case "m":
                return 22;
            case "n":
                return 23;
            case "o":
                return 24;
            case "p":
                return 25;
            case "q":
                return 26;
            case "r":
                return 27;
            case "s":
                return 28;
            case "t":
                return 29;
            case "u":
                return 30;
            case "v":
                return 31;
            case "w":
                return 32;
            case "x":
                return 33;
            case "y":
                return 34;
            case "z":
                return 35;
            default:
                return 0;
        }
    }
}
