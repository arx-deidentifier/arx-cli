package org.deidentifier.arx.cli.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model for distinct d-presence
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * 
 */
public class DPresence extends Criterion {

    /**
     * Parses the given criterion string and returns the model
     * @param criterion
     * @return
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

    private static final String  name;
    private static final String  regex;
    private static final Pattern pattern;

    private static Matcher       matcher;

    static {
        name = "-PRESENCE";
        regex = "\\((.*?),(.*?)\\)" + name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
    private final double         dMin;

    private final double         dMax;

    public DPresence(double dMin, double dMax) {
        this.dMin = dMin;
        this.dMax = dMax;
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
        DPresence other = (DPresence) obj;
        if (Double.doubleToLongBits(dMax) != Double.doubleToLongBits(other.dMax)) {
            return false;
        }
        if (Double.doubleToLongBits(dMin) != Double.doubleToLongBits(other.dMin)) {
            return false;
        }
        return true;
    }

    public double getDMax() {
        return dMin;
    }

    public double getDMin() {
        return dMin;
    }

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

    @Override
    public String toString() {
        return "(" + dMin + "," + dMax + ")" + name;
    }

}
