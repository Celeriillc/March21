package com.celerii.celerii.helperClasses;

/**
 * Created by DELL on 3/26/2019.
 */

public class StringComparer {
    public static boolean contains(String searchTerm, String mainTerm) {
        boolean contains = true;
        boolean containsInnerLoop = false;

        searchTerm = searchTerm.trim();
        searchTerm = searchTerm.replace(".", "");
        searchTerm = searchTerm.replace(",", "");
        String[] searchTermSplit = searchTerm.split(" ");
        String[] mainTermSplit = mainTerm.split(" ");

        if (searchTermSplit.length == 0 || mainTermSplit.length == 0) {
            return false;
        }

        for (int i = 0; i < searchTermSplit.length; i++) {
            for (int j = 0; j < mainTermSplit.length; j++) {
                if (mainTermSplit[j].toLowerCase().contains(searchTermSplit[i].toLowerCase())) {
                    containsInnerLoop = true;
                    break;
                } else {
                    containsInnerLoop = false;
                }
            }
            contains = contains && containsInnerLoop;
        }

        return contains;
    }
}
