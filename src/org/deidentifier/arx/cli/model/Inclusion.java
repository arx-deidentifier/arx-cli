package org.deidentifier.arx.cli.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for inclusion
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * 
 */
public class Inclusion extends Criterion {

    /**
     * Parses the given criterion string and returns the model
     * @param criterion
     * @return
     */
    public static Criterion parse(String criterion) {
        matcher = pattern.matcher(criterion);
        if (matcher.find()) {
            return new Inclusion();
        } else {
            return null;
        }
    }

    private static final String  name;
    private static final String  regex;
    private static final Pattern pattern;

    private static Matcher       matcher;

    static {
        name = "INCLUSION";
        regex = name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public Inclusion() {
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
