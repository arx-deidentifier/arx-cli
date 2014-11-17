package org.deidentifier.arx.cli.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for inclusion.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class Inclusion extends Criterion {

    /**
     * Parses the given criterion string and returns the model.
     *
     * @param criterion the criterion
     * @return the criterion
     */
    public static Criterion parse(String criterion) {
        matcher = pattern.matcher(criterion);
        if (matcher.find()) {
            return new Inclusion();
        } else {
            return null;
        }
    }

    /** The Constant name. */
    private static final String  name;
    
    /** The Constant regex. */
    private static final String  regex;
    
    /** The Constant pattern. */
    private static final Pattern pattern;

    /** The matcher. */
    private static Matcher       matcher;

    static {
        name = "INCLUSION";
        regex = name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Instantiates a new inclusion.
     */
    public Inclusion() {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.cli.model.Criterion#toString()
     */
    @Override
    public String toString() {
        return name;
    }

}
