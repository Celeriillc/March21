package com.celerii.celerii.helperClasses;

/**
 * Created by DELL on 1/13/2019.
 */

public class DumbNumericals {
    public static int maxOfFiveInts(int A, int B, int C, int D, int E){
        if (A >= B && A >= C && A >= D && A >= E) { return A; }
        else if (B >= A && B >= C && B >= D && B >= E) { return B; }
        else if (C >= A && C >= B && C >= D && C >= E) { return C; }
        else if (D >= A && D >= B && D >= C && D >= E) { return D; }
        else if (E >= A && E >= B && E >= C && E >= D) { return E; }
        return A;
    }

    public static int generateIntIDFromStringID(String stringID) {
        if (stringID == null) { return 0; }
        if (stringID.equals("")) { return 0; }

        int intID = 0;
        char[] charArray = stringID.toCharArray();

        for (int i = 0;  i < charArray.length; i++) {
            char character = charArray[i];
            if (character == 'A' || character == 'a') {
                intID += 1;
            } else if (character == 'B' || character == 'b') {
                intID += 2;
            } else if (character == 'C' || character == 'c') {
                intID += 3;
            } else if (character == 'D' || character == 'd') {
                intID += 4;
            } else if (character == 'E' || character == 'e') {
                intID += 5;
            } else if (character == 'F' || character == 'f') {
                intID += 6;
            } else if (character == 'G' || character == 'g') {
                intID += 7;
            } else if (character == 'H' || character == 'h') {
                intID += 8;
            } else if (character == 'I' || character == 'i') {
                intID += 9;
            } else if (character == 'J' || character == 'j') {
                intID += 10;
            } else if (character == 'K' || character == 'k') {
                intID += 11;
            } else if (character == 'L' || character == 'l') {
                intID += 12;
            } else if (character == 'M' || character == 'm') {
                intID += 13;
            } else if (character == 'N' || character == 'n') {
                intID += 14;
            } else if (character == 'O' || character == 'o') {
                intID += 15;
            } else if (character == 'P' || character == 'p') {
                intID += 16;
            } else if (character == 'Q' || character == 'q') {
                intID += 17;
            } else if (character == 'R' || character == 'r') {
                intID += 18;
            } else if (character == 'S' || character == 's') {
                intID += 19;
            } else if (character == 'T' || character == 't') {
                intID += 20;
            } else if (character == 'U' || character == 'u') {
                intID += 21;
            } else if (character == 'V' || character == 'v') {
                intID += 22;
            } else if (character == 'W' || character == 'w') {
                intID += 23;
            } else if (character == 'X' || character == 'x') {
                intID += 24;
            } else if (character == 'Y' || character == 'y') {
                intID += 25;
            } else if (character == 'Z' || character == 'z') {
                intID += 26;
            } else if (character == '0' || character == '1' || character == '2' || character == '3' || character == '4' || character == '5' || character == '6' || character == '7' || character == '8' || character == '9') {
                intID += (int) character;
            }
        }

        return intID;
    }
}
