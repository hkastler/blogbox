package com.hkstlr.blogbox.control;

public class StringChanger {

    private StringChanger() {
    	//
    }

    /**
     * Replaces occurrences of non-alphanumeric characters with a supplied
     * char.
     *
     * @param str
     * @param subst
     * @return
     */
    public static String replaceNonAlphanumeric(String str, char subst) {
        StringBuilder ret = new StringBuilder(str.length());
        char[] testChars = str.toCharArray();
        for (int i = 0; i < testChars.length; i++) {
            if (Character.isLetterOrDigit(testChars[i])) {
                ret.append(testChars[i]);
            } else {
                ret.append(subst);
            }
        }
        return ret.toString();
    }
}