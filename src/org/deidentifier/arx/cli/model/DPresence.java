package org.deidentifier.arx.cli.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for distinct d-presence.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class DPresence extends Criterion {

    /**
     * Parses the given criterion string and returns the model.
     *
     * @param criterion the criterion
     * @return the criterion
     */
    public static Criterion parse(String criterion) {
        matcher = pattern.matcher(criterion);
        if (matcher.find()) {
            final double dmin = Double.parseDouble(matcher.group(1));
            final double dmax = Double.parseDouble(matcher.group(2));
            return new DPresence(dmin, dmax);
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
        name = "-PRESENCE";
        regex = "\\((.*?),(.*?)\\)" + name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
    
    /** The d min. */
    private final double         dMin;

    /** The d max. */
    private final double         dMax;

    /**
     * Instantiates a new d presence.
     *
     * @param dMin the d min
     * @param dMax the d max
     */
    public DPresence(double dMin, double dMax) {
        this.dMin = dMin;
        this.dMax = dMax;
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
        DPresence other = (DPresence) obj;
        if (Double.doubleToLongBits(dMax) != Double.doubleToLongBits(other.dMax)) {
            return false;
        }
        if (Double.doubleToLongBits(dMin) != Double.doubleToLongBits(other.dMin)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the d max.
     *
     * @return the d max
     */
    public double getDMax() {
        return dMin;
    }

    /**
     * Gets the d min.
     *
     * @return the d min
     */
    public double getDMin() {
        return dMin;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(dMax);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dMin);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.cli.model.Criterion#toString()
     */
    @Override
    public String toString() {
        return "(" + dMin + "," + dMax + ")" + name;
    }

}
