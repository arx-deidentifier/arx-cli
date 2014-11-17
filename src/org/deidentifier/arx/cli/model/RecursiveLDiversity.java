package org.deidentifier.arx.cli.model;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deidentifier.arx.cli.ParseUtil;

/**
 * The model for recursive-(c,l)-diversity.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class RecursiveLDiversity extends Criterion {

    /**
     * Parses the given criterion string and returns the model.
     *
     * @param criterion the criterion
     * @return the criterion
     * @throws ParseException the parse exception
     */
    public static Criterion parse(String criterion) throws ParseException {
        return parse(null, criterion, 'X');
    }

    /**
     * Parses the given criterion string and returns the model.
     *
     * @param criterion the criterion
     * @param seperatorKeyValue the seperator key value
     * @return the criterion
     * @throws ParseException the parse exception
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
     * Parses the given criterion string and returns the model.
     *
     * @param attribute the attribute
     * @param criterion the criterion
     * @param seperatorKeyValue the seperator key value
     * @return the criterion
     */
    private static Criterion parse(String attribute, String criterion, char seperatorKeyValue) {
        matcher = pattern.matcher(criterion);
        if (matcher.find()) {
            final double c = Double.parseDouble(matcher.group(1));
            final int l = (int) Double.parseDouble(matcher.group(2));
            return new RecursiveLDiversity(attribute, c, l, seperatorKeyValue);
        } else {
            return null;
        }
    }

    /** The Constant name. */
    private static final String  name;
    
    /** The Constant prefix. */
    private static final String  prefix;
    
    /** The Constant regex. */
    private static final String  regex;
    
    /** The Constant pattern. */
    private static final Pattern pattern;

    /** The matcher. */
    private static Matcher       matcher;

    static {
        name = "-DIVERSITY";
        prefix = "RECURSIVE-";
        regex = prefix + "\\((.*?),(.*?)\\)" + name;
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /** The l. */
    private final int            l;
    
    /** The c. */
    private final double         c;
    
    /** The seperator key value. */
    private final char           seperatorKeyValue;
    
    /** The attribute. */
    private final String         attribute;

    /**
     * Instantiates a new recursive l diversity.
     *
     * @param attribute the attribute
     * @param c the c
     * @param l the l
     * @param seperatorKeyValue the seperator key value
     */
    public RecursiveLDiversity(String attribute, double c, int l, char seperatorKeyValue) {
        this.attribute = attribute;
        this.l = l;
        this.c = c;
        this.seperatorKeyValue = seperatorKeyValue;
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
        RecursiveLDiversity other = (RecursiveLDiversity) obj;
        if (attribute == null) {
            if (other.attribute != null) {
                return false;
            }
        } else if (!attribute.equals(other.attribute)) {
            return false;
        }
        if (Double.doubleToLongBits(c) != Double.doubleToLongBits(other.c)) {
            return false;
        }
        if (l != other.l) {
            return false;
        }
        return true;
    }

    /**
     * Gets the attribute.
     *
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Gets the c.
     *
     * @return the c
     */
    public double getC() {
        return c;
    }

    /**
     * Gets the l.
     *
     * @return the l
     */
    public int getL() {
        return l;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((attribute == null) ? 0 : attribute.hashCode());
        long temp;
        temp = Double.doubleToLongBits(c);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        result = (prime * result) + l;
        return result;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.cli.model.Criterion#toString()
     */
    @Override
    public String toString() {
        return (attribute == null ? "" : attribute + seperatorKeyValue) + prefix + "(" + c + "," + l + ")" + name;
    }

}
