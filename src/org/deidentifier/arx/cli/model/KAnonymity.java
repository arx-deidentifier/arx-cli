package org.deidentifier.arx.cli.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for k-anonymity
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * 
 */
public class KAnonymity extends Criterion {

    /**
     * Parses the given criterion string and returns the model
     * @param criterion
     * @return
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

    private static final String  name;
    private static final String  regex;
    private static final Pattern pattern;

    private static Matcher       matcher;

    static {
        name = "-ANONYMITY";
        regex = "(\\d*?)" + name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private final int            k;

    public KAnonymity(int k) {
        this.k = k;
    }

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

    public int getK() {
        return k;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + k;
        return result;
    }

    @Override
    public String toString() {
        return k + name;
    }

}
