package org.deidentifier.arx.cli;

import java.util.regex.Pattern;
/**
 * A util class
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * 
 */
public class ParseUtil {

    /**
     * Splits the splitString by means of the separator. Escaping via backslash allowed, the escape character will be removed.
     * 
     * @param splitString
     * @param separator
     * @return
     */
    public static String[] splitEscapedStringBySeparator(final String splitString, final char separator) {
        if ((splitString != null) && (splitString.length() > 0)) {
            final char escape = '\\';
            final String regex = "(?<!" + Pattern.quote(String.valueOf(escape)) + ")" + Pattern.quote(String.valueOf(separator));
            final String[] splittedString = splitString.split(regex);
            for (int i = 0; i < splittedString.length; i++) {
                splittedString[i] = splittedString[i].replaceAll(Pattern.quote(String.valueOf(escape)) +
                                                                 Pattern.quote(String.valueOf(separator)),
                                                                 String.valueOf(separator));
            }
            return splittedString;
        } else {
            return new String[0];
        }
    }

    /**
     * Escapes the given string. All occurrences of separator will be escaped via a backslash.
     * 
     * @param string
     * @param separator
     * @return
     */
    public static String toEscapedString(final String string, final char separator) {
        if ((string != null) && (string.length() > 0)) {
            final String replaced = string.replaceAll(Pattern.quote(String.valueOf(separator)), "\\\\" + String.valueOf(separator));
            return replaced;
        } else {
            return "";
        }
    }

    public static final char SEPARATOR_CRITERIA  = ',';
    public static final char SEPARATOR_KEY_VALUE = '=';

}
