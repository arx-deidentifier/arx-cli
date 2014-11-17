package org.deidentifier.arx.cli.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.cli.ParseUtil;

/**
 * Base class for the criteria.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public abstract class Criterion {

    /**
     * Creates a list of Criterion objects from the given criteria string.
     *
     * @param criteria the criteria
     * @param seperator the seperator
     * @return the list
     * @throws ParseException the parse exception
     */
    public static List<Criterion> create(String criteria, char seperator) throws ParseException {
        // cleanup string remove potential leading [ and trailing ]
        criteria = criteria.startsWith("[") ? criteria.substring(1) : criteria;
        criteria = criteria.endsWith("]") ? criteria.substring(0, criteria.length() - 1) : criteria;

        // trim string
        criteria = criteria.trim();

        // split string
        final String[] criteriaArray = ParseUtil.splitEscapedStringBySeparator(criteria, seperator);

        List<Criterion> criteriaModel = new ArrayList<Criterion>();

        for (int i = 0; i < criteriaArray.length; i++) {

            // trim string
            String criterionString = criteriaArray[i].trim();

            // try to generate the criterion
            Criterion criterion = null;

            // k-anyonymity
            criterion = KAnonymity.parse(criterionString);
            if (criterion != null) {
                criteriaModel.add(criterion);
            }

            // d-presence
            criterion = DPresence.parse(criterionString);
            if (criterion != null) {
                criteriaModel.add(criterion);
            }

            // inclusion
            criterion = Inclusion.parse(criterionString);
            if (criterion != null) {
                criteriaModel.add(criterion);
            }

            // check if criterion contains key=value pair
            if (criterionString.contains(String.valueOf(ParseUtil.SEPARATOR_KEY_VALUE))) {

                // distinct-l-diversity
                criterion = DistinctLDiversity.parse(criterionString, ParseUtil.SEPARATOR_KEY_VALUE);
                if (criterion != null) {
                    criteriaModel.add(criterion);
                }

                // entropy-l-diversity
                criterion = EntropyLDiversity.parse(criterionString, ParseUtil.SEPARATOR_KEY_VALUE);
                if (criterion != null) {
                    criteriaModel.add(criterion);
                }

                // recursive-c-l-diversity
                criterion = RecursiveLDiversity.parse(criterionString, ParseUtil.SEPARATOR_KEY_VALUE);
                if (criterion != null) {
                    criteriaModel.add(criterion);
                }

                // hierarchical-t-closeness
                criterion = HierarchicalTCloseness.parse(criterionString, ParseUtil.SEPARATOR_KEY_VALUE);
                if (criterion != null) {
                    criteriaModel.add(criterion);
                }

                // equal-t-closeness
                criterion = EqualTCloseness.parse(criterionString, ParseUtil.SEPARATOR_KEY_VALUE);
                if (criterion != null) {
                    criteriaModel.add(criterion);
                }
            }

            // sanity check
            if (criteriaModel.size() != (i + 1)) {
                throw new ParseException("Criterion number " + (i + 1) + " could not be parsed: [" + criterionString + "]", i + 1);
            }

        }

        return criteriaModel;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();
}
