package org.deidentifier.arx.cli.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for k-anonymity.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class KAnonymity extends Criterion {

    /**
     * Parses the given criterion string and returns the model.
     *
     * @param criterion the criterion
     * @return the criterion
     */
    public static Criterion parse(String criterion) {
        matcher = pattern.matcher(criterion);
        if (matcher.find()) {
            final int k = Integer.parseInt(matcher.group(1));
            return new KAnonymity(k);
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
        name = "-ANONYMITY";
        regex = "(\\d*?)" + name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /** The k. */
    private final int            k;

    /**
     * Instantiates a new k anonymity.
     *
     * @param k the k
     */
    public KAnonymity(int k) {
        this.k = k;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KAnonymity other = (KAnonymity) obj;
        if (k != other.k) {
            return false;
        }
        return true;
    }

    /**
     * Gets the k.
     *
     * @return the k
     */
    public int getK() {
        return k;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + k;
        return result;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.cli.model.Criterion#toString()
     */
    @Override
    public String toString() {
        return k + name;
    }

}
