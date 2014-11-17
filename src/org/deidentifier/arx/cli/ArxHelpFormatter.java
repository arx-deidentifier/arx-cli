package org.deidentifier.arx.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import joptsimple.HelpFormatter;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionDescriptor;

/**
 * The Class ArxHelpFormatter.
 */
public class ArxHelpFormatter implements HelpFormatter {

    /** The Constant NEWLINE. */
    private static final String NEWLINE = System.getProperty("line.separator");

    /*
     * (non-Javadoc)
     * 
     * @see joptsimple.HelpFormatter#format(java.util.Map)
     */
    @Override
    public String format(Map<String, ? extends OptionDescriptor> options) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Usage:" + NEWLINE);

        // remove duplicates
        HashSet<OptionDescriptor> set = new HashSet<OptionDescriptor>(options.values());

        List<String> optionStrings = new ArrayList<String>();
        for (OptionDescriptor option : set) {
            optionStrings.add(lineFor(option));
        }

        // sort options lexicographically
        Collections.sort(optionStrings);

        for (String string : optionStrings) {
            buffer.append(string);
        }

        return buffer.toString();
    }

    /**
     * Line for.
     *
     * @param descriptor the descriptor
     * @return the string
     */
    private String lineFor(OptionDescriptor descriptor) {

        if (!(descriptor instanceof NonOptionArgumentSpec<?>)) {

            StringBuilder line = new StringBuilder();

            String[] optionString = new String[2];
            Collection<String> options = descriptor.options();
            for (String option : options) {
                if (option.length() < 3) { // TODO: Ugly hack
                    optionString[0] = option;
                } else {
                    optionString[1] = option;
                }
            }

            line.append("-" + optionString[0]);
            line.append(", ");
            if (optionString[1].length() > 16) {
                line.append(NEWLINE + "  ");
            }
            line.append("--" + optionString[1]);

            if (descriptor.isRequired()) {
                line.append("*");
            }
            if (line.length() < 16) {
                line.append("\t");
            }

            line.append("\t");
            line.append(descriptor.description());

            line.append(System.getProperty("line.separator"));
            return line.toString();
        } else {
            return "";
        }
    }
}
