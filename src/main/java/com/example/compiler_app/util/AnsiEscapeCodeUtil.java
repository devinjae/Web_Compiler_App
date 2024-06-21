package com.example.compiler_app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsiEscapeCodeUtil {
    private static final String ANSI_REGEX = "\\u001B\\[[;\\d]*m";

    /**
     * Removes ANSI escape codes from the given input string.
     * @param input String containing ANSI escape codes
     * @return Cleaned string with ANSI escape codes removed
     */
    public static String removeAnsiEscapeCodes(String input) {
        Pattern pattern = Pattern.compile(ANSI_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }
}
