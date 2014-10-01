package org.deidentifier.arx.cli.model;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deidentifier.arx.cli.ParseUtil;

/**
 * The model for t-closeness with equal-distance EMD
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * 
 */
public class EqualTCloseness extends Criterion {

    /**
     * Parses the given criterion string and returns the model
     * @param criterion
     * @return
     * @throws ParseException
     */
    public static Criterion parse(String criterion) throws ParseException {
        return parse(null, criterion, 'X');
    }

    /**
     * Parses the given criterion string and returns the model
     * @param criterion
     * @param seperatorKeyValue
     * @return
     * @throws ParseException
     */
    public static Criterion parse(String criterion, char seperatorKeyValue) throws ParseException {
        String[] split = ParseUtil.splitEscapedStringBySeparator(criterion, seperatorKeyValue);
        if (split.length != 2) {
            throw new ParseException("Failed to parse [" + criterion + "] - Syntax: attributeName=criterionDefinition.", 1);
        }
        String key = split[0];
        String value = split[1];
        return parse(key, value, seperatorKeyValue);
    }

    /**
     * Parses the given criterion string and returns the model
     * @param attribute
     * @param criterion
     * @param seperatorKeyValue
     * @return
     */
    private static Criterion parse(String attribute, String criterion, char seperatorKeyValue) {
        matcher = pattern.matcher(criterion);
        if (matcher.find()) {
            final double t = Double.parseDouble(matcher.group(1));
            return new EqualTCloseness(attribute, t, seperatorKeyValue);
        } else {
            return null;
        }
    }

    private static final String  name;
    private static final String  prefix;
    private static final String  regex;
    private static final Pattern pattern;

    private static Matcher       matcher;

    static {
        name = "-CLOSENESS";
        prefix = "EQUALDISTANCE-";
        regex = prefix + "\\((.*?)\\)" + name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private final double         t;
    private final char           seperatorKeyValue;
    private final String         attribute;

    public EqualTCloseness(String attribute, double t, char seperatorKeyValue) {
        this.attribute = attribute;
        this.t = t;
        this.seperatorKeyValue = seperatorKeyValue;
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
        EqualTCloseness other = (EqualTCloseness) obj;
        if (attribute == null) {
            if (other.attribute != null) {
                return false;
            }
        } else if (!attribute.equals(other.attribute)) {
            return false;
        }
        if (Double.doubleToLongBits(t) != Double.doubleToLongBits(other.t)) {
            return false;
        }
        return true;
    }

    public String getAttribute() {
        return attribute;
    }

    public double getT() {
        return t;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((attribute == null) ? 0 : attribute.hashCode());
        long temp;
        temp = Double.doubleToLongBits(t);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return (attribute == null ? "" : attribute + seperatorKeyValue) + prefix + "(" + t + ")" + name;
    }

}
