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
}
