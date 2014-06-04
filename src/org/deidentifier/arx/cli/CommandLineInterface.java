/*
 * ARX: Efficient, Stable and Optimal Data Anonymization
 * Copyright (C) 2012 - 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.deidentifier.arx.cli;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSelector;
import org.deidentifier.arx.DataSubset;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EntropyLDiversity;
import org.deidentifier.arx.criteria.EqualDistanceTCloseness;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.PrivacyCriterion;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;

/**
 * A simple command-line client
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * 
 */
public class CommandLineInterface {

    /**
     * --quasiidentifying [attributname1,attributname2,...]
     * -qi
     * 
     * --sensitive [attributname1,attributname2,...]
     * -se
     * 
     * --insensitive [attributname1,attributname2,...]
     * -is
     * 
     * --identifying [attributname1,attributname2,...]
     * -id
     * 
     * --hierarchies [attributname1=filename1,attributname2=filename2]
     * -h
     * 
     * --datatype [attributname1=STRING|DECIMAL(format)|INTEGER|DATE(format)]
     * -d
     * 
     * --criteria [x-ANONYMITY,(x,y)-PRESENCE,attributname1=DISTINCT|ENTROPY|RECURSIVE-(x|x,y)-DIVERSITY,attributname2=HIERARCHICAL|EQUALDISTANCE-(x)-CLOSENESS]
     * -c
     * 
     * --metric [DM|DMSTAR|ENTROPY|HEIGHT|NMENTROPY|PREC|AECS]
     * -m
     * 
     * --suppression [value]
     * -s
     * 
     * --database [TYPE=[MYSQL|POSTGRESQL|SQLLITE],URL=value,PORT=value,USER=value,PASSWORD=value,DATABASE=value,TABLE=value]
     * -db
     * 
     * --file [filename]
     * -f
     * 
     * --output [filename]
     * -o
     * 
     * --researchsubset [FILE=filename|QUERY=querystring]
     * -r
     * 
     * --separator [char|DETECT]
     * -sep
     * 
     * --practicalmonotonicity [TRUE|FALSE]
     * -pm
     * 
     * 
     */

    public static enum Metric {
        AECS,
        DM,
        DMSTAR,
        ENTROPY,
        HEIGHT,
        NMENTROPY,
        PREC
    }

    /**
     * Lets do it!.
     * 
     * @param args the arguments
     */
    public static void main(final String[] args) {
        final CommandLineInterface cli = new CommandLineInterface();
        cli.run(args);
    }

    private Data buildDataObject(final File input, final String database, final char separator) throws IOException {
        // build data object
        // TODO: currently only file input supported... Implement JDBC and Sysin
        // TODO: for DataSource a addAllCoulumns could be introduced
        final Data data = Data.create(input, separator);
        return data;
    }

    private List<PrivacyCriterion> parseCriteria(final String criteriaOption, final Map<String, Hierarchy> hierarchies, final DataSubset subset) {
        final List<PrivacyCriterion> criteria = new ArrayList<PrivacyCriterion>();

        final String k_anonymityRegEx = "(\\d)-ANONYMITY";
        final String d_presenceRegEx = "[(](\\d+.?\\d*),(\\d+.?\\d*)[)]-PRESENCE";
        final String attributeNameRegEx = "(\\w*)=";
        final String l_diversityRegEx = "-[(](\\d.?\\d?)[)]-DIVERSITY";
        final String l_diversityRegEx_Recursive = "-[(](\\d.?\\d?),(\\d.?\\d?)[)]-DIVERSITY";
        final String t_closenessRegEx = "-[(](\\d.?\\d?)[)]-CLOSENESS";

        Pattern pattern = null;
        Matcher matcher = null;

        // add all k-anonymity criteria
        pattern = Pattern.compile(k_anonymityRegEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            final int k = Integer.parseInt(matcher.group(1));
            final PrivacyCriterion criterion = new KAnonymity(k);
            criteria.add(criterion);
        }

        // add all d-presence criteria
        pattern = Pattern.compile(d_presenceRegEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            if (subset == null) {
                throw new IllegalArgumentException("for d-presence a subset has to be defined");
            }
            final double dmin = Double.parseDouble(matcher.group(1));
            final double dmax = Double.parseDouble(matcher.group(2));
            final PrivacyCriterion criterion = new DPresence(dmin, dmax, subset);
            criteria.add(criterion);
        }

        // add all l-diversity criteria

        // distinct
        pattern = Pattern.compile(attributeNameRegEx + "DISTINCT" + l_diversityRegEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            final String attributeName = matcher.group(1);
            final int l = Integer.parseInt(matcher.group(2));
            final PrivacyCriterion criterion = new DistinctLDiversity(attributeName, l);
            criteria.add(criterion);
        }

        // entropy
        pattern = Pattern.compile(attributeNameRegEx + "ENTROPY" + l_diversityRegEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            final String attributeName = matcher.group(1);
            final double l = Double.parseDouble(matcher.group(2));
            final PrivacyCriterion criterion = new EntropyLDiversity(attributeName, l);
            criteria.add(criterion);
        }

        // recursive
        pattern = Pattern.compile(attributeNameRegEx + "RECURSIVE" + l_diversityRegEx_Recursive, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            final String attributeName = matcher.group(1);
            final double c = Double.parseDouble(matcher.group(2));
            final int l = Integer.parseInt(matcher.group(3));
            final PrivacyCriterion criterion = new RecursiveCLDiversity(attributeName, c, l);
            criteria.add(criterion);
        }

        // add all t-closeness criteria

        // hierarchical
        pattern = Pattern.compile(attributeNameRegEx + "HIERARCHICAL" + t_closenessRegEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            final String attributeName = matcher.group(1);
            final Hierarchy h = hierarchies.get(attributeName);
            if (h == null) {
                throw new IllegalArgumentException("for hierarchical t-closeness a hierarchy has to be defined: " + attributeName);
            }
            final double t = Double.parseDouble(matcher.group(2));
            final PrivacyCriterion criterion = new HierarchicalDistanceTCloseness(attributeName, t, h);
            criteria.add(criterion);
        }

        // equal
        pattern = Pattern.compile(attributeNameRegEx + "EQUALDISTANCE" + t_closenessRegEx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(criteriaOption);
        while (matcher.find()) {
            final String attributeName = matcher.group(1);
            final double t = Double.parseDouble(matcher.group(2));
            final PrivacyCriterion criterion = new EqualDistanceTCloseness(attributeName, t);
            criteria.add(criterion);
        }

        return criteria;
    }

    private Map<String, DataType<?>> parseDataTypes(final List<String> datatypeOption) {
        final Map<String, DataType<?>> datatypes = new HashMap<String, DataType<?>>();
        for (final String type : datatypeOption) {
            final String[] split = type.split("=");

            final Pattern pattern = Pattern.compile("(\\w+)[(]?(.*)", Pattern.CASE_INSENSITIVE);
            final Matcher matcher = pattern.matcher(split[1]);
            while (matcher.find()) {
                final String datatype = matcher.group(1).toUpperCase();
                final String f = matcher.group(2);
                String format = "";
                if (f.length() > 0) {
                    format = f.substring(0, f.length() - 1);
                }
                switch (datatype) {
                case "STRING":
                    datatypes.put(split[0], DataType.STRING);
                    break;
                case "INTEGER":
                    datatypes.put(split[0], DataType.INTEGER);
                    break;
                case "DECIMAL":
                    datatypes.put(split[0], DataType.createDecimal(format));
                    break;
                case "DATE":
                    datatypes.put(split[0], DataType.createDate(format));
                    break;
                default:
                    throw new IllegalArgumentException("datatype not recognized: " + datatype);
                }
            }
        }
        return datatypes;
    }

    private Map<String, Hierarchy> parseHierarchies(final String hierarchyOption, final char seperator) throws IOException {
        final Map<String, Hierarchy> hierarchies = new HashMap<String, Hierarchy>();

        // TODO: with the current implementation a filename is not allowed to contain ','

        // different naming scheme?
        // in windows the following charcters are not allowed in filenames:
        // < > ? " : | \ / *
        // String hiers2 = "attributname1=filename1/fed=te.csv:attributname2=filename2";

        StringBuilder attributeName = new StringBuilder();
        StringBuilder fileName = new StringBuilder();
        boolean matchingAttributeName = true;
        for (int i = 0; i < hierarchyOption.length(); i++) {
            final char c = hierarchyOption.charAt(i);
            if ((c == '=') && matchingAttributeName) {
                matchingAttributeName = false;
            } else if ((c == ',') && !matchingAttributeName) {
                final Hierarchy h = Hierarchy.create(fileName.toString(), seperator);
                hierarchies.put(attributeName.toString(), h);
                matchingAttributeName = true;
                attributeName = new StringBuilder();
                fileName = new StringBuilder();
            } else if (matchingAttributeName) {
                attributeName.append(c);
            } else if (!matchingAttributeName) {
                fileName.append(c);
            }
        }
        // put last found pair in map
        if (attributeName.length() > 0) {
            final Hierarchy h = Hierarchy.create(fileName.toString(), seperator);
            hierarchies.put(attributeName.toString(), h);
        }

        return hierarchies;
    }

    private char parseSeparator(final String separatorOption) {
        if (separatorOption.length() == 1) {
            return separatorOption.charAt(0);
        } else if (separatorOption.equalsIgnoreCase("DETECT")) {
            // TODO: Implement automatic detection
            throw new UnsupportedOperationException("automatic detection of seperator is currently not supported.");
        } else {
            throw new IllegalArgumentException("only a single character or the keyword 'DETECT' is allowed");
        }
    }

    private DataSubset parseSubset(final String subsetOption, final char separator, final Data data) throws ParseException, IOException {

        DataSubset subset = null;

        if (subsetOption != null) {

            final Pattern pattern = Pattern.compile("(\\w+)=(.*)", Pattern.CASE_INSENSITIVE);
            final Matcher matcher = pattern.matcher(subsetOption);
            while (matcher.find()) {
                final String type = matcher.group(1).toUpperCase();
                final String content = matcher.group(2);

                switch (type) {
                case "FILE":
                    subset = DataSubset.create(data, Data.create(content, separator));
                    break;
                case "QUERY":
                    final DataSelector selector = DataSelector.create(data, content);
                    subset = DataSubset.create(data, selector);
                    break;
                default:
                    throw new IllegalArgumentException("subset specification not recognized: " + type);
                }
            }
        }

        return subset;
    }

    private void run(final String[] args) {
        final OptionParser parser = new OptionParser();

        // define options

        // attributes
        final OptionSpec<String> qiOption = parser.acceptsAll(Arrays.asList("qi", "quasiidentifying"), "names of the quasi identifying attributes, delimited by ','").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        final OptionSpec<String> seOption = parser.acceptsAll(Arrays.asList("se", "sensitive"), "names of the sensitive attributes, delimited by ','").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        final OptionSpec<String> isOption = parser.acceptsAll(Arrays.asList("is", "insensitive"), "names of the insensitive attributes, delimited by ','").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        final OptionSpec<String> idOption = parser.acceptsAll(Arrays.asList("id", "identifying"), "names of the identifying attributes, delimited by ','").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');

        // hierarchies
        final OptionSpec<String> hierarchyOption = parser.acceptsAll(Arrays.asList("h", "hierarchies"), "hierarchies for the attributes, delimited by ','. Syntax: [attributname1=filename1,attributname2=filename2]").withRequiredArg().ofType(String.class);

        // datatypes
        final OptionSpec<String> dataTypeOption = parser.acceptsAll(Arrays.asList("d", "datatype"), "datatypes of the attributes, delimited by ','. Syntax: [attributname1=STRING|DECIMAL(format)|INTEGER|DATE(format)]").withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');

        // criteria
        final OptionSpec<String> criteriaOption = parser.acceptsAll(Arrays.asList("c", "criteria"), "anonymization criteria, delimited by ','. Syntax: [x-ANONYMITY,(x,y)-PRESENCE,attributname1=DISTINCT|ENTROPY|RECURSIVE-(x|x,y)-DIVERSITY,attributname2=HIERARCHICAL|EQUALDISTANCE-(x)-CLOSENESS]").withRequiredArg().ofType(String.class);

        // metric
        final OptionSpec<String> metricOption = parser.acceptsAll(Arrays.asList("m", "metric"), "information loss metric, possible values " + Arrays.toString(Metric.values())).withRequiredArg().ofType(String.class).defaultsTo("ENTROPY");

        // suppression
        final OptionSpec<Double> supressionOption = parser.acceptsAll(Arrays.asList("s", "suppression"), "amount of allowed outlier (supression) in percent/100. e.g. 0.5 means 50% outlier allowed").withRequiredArg().ofType(Double.class).defaultsTo(0.0d);

        // database
        final OptionSpec<String> databaseOption = parser.acceptsAll(Arrays.asList("db", "database"), "connection information for importing data from a database table. Syntax: [TYPE=[MYSQL|POSTGRESQL|SQLLITE],URL=value,PORT=value,USER=value,PASSWORD=value,DATABASE=value,TABLE=value] ").withRequiredArg().ofType(String.class);

        // file
        final OptionSpec<File> fileOption = parser.acceptsAll(Arrays.asList("f", "file"), "filename of the input data").withRequiredArg().ofType(File.class);

        // output
        final OptionSpec<File> outputOption = parser.acceptsAll(Arrays.asList("o", "output"), "filename of anonymized output").withRequiredArg().ofType(File.class);

        // research subset
        final OptionSpec<String> researchSubsetOption = parser.acceptsAll(Arrays.asList("r", "researchsubset"), "specification of a research subset, either by specifying a file or a query. Syntax: [FILE=filename|QUERY=querystring]").withRequiredArg().ofType(String.class);

        // separator
        final OptionSpec<String> separatorOption = parser.acceptsAll(Arrays.asList("sep", "separator"), "seperator used in the sepcified files; if omitted ';' is assumed. Syntax: [char|DETECT]").withRequiredArg().ofType(String.class).defaultsTo(";");

        // practical monotonicity
        final OptionSpec<Boolean> practicalOption = parser.acceptsAll(Arrays.asList("pm", "practicalmonotonicity"), "if present, practical monotonicity is assumed").withOptionalArg().ofType(Boolean.class).defaultsTo(false);

        try {
            final OptionSet options = parser.parse(args);

            final char separator = parseSeparator(options.valueOf(separatorOption));
            final boolean practicalMonotonicity = options.valueOf(practicalOption);
            final Map<String, Hierarchy> hierarchies = parseHierarchies(options.valueOf(hierarchyOption), separator);

            final File input = options.valueOf(fileOption);
            final String database = options.valueOf(databaseOption);

            final Data data = buildDataObject(input, database, separator);

            final DataSubset subset = parseSubset(options.valueOf(researchSubsetOption), separator, data);
            final List<PrivacyCriterion> criteria = parseCriteria(options.valueOf(criteriaOption), hierarchies, subset);

            final File output = options.valueOf(outputOption);

            final double supression = options.valueOf(supressionOption);

            // set metric
            final Metric mValue = Metric.valueOf(options.valueOf(metricOption).trim().toUpperCase());
            org.deidentifier.arx.metric.Metric<?> metric = null;
            switch (mValue) {
            case PREC:
                metric = org.deidentifier.arx.metric.Metric.createPrecisionMetric();
                break;
            case HEIGHT:
                metric = org.deidentifier.arx.metric.Metric.createHeightMetric();
                break;
            case DMSTAR:
                metric = org.deidentifier.arx.metric.Metric.createDMStarMetric();
                break;
            case DM:
                metric = org.deidentifier.arx.metric.Metric.createDMMetric();
                break;
            case ENTROPY:
                metric = org.deidentifier.arx.metric.Metric.createEntropyMetric();
                break;
            case NMENTROPY:
                metric = org.deidentifier.arx.metric.Metric.createNMEntropyMetric();
                break;
            case AECS:
                metric = org.deidentifier.arx.metric.Metric.createAECSMetric();
                break;
            default:
                throw new IllegalArgumentException("metric unknown: " + mValue);
            }

            final List<String> quasiIdentifier = options.valuesOf(qiOption);
            final List<String> sensitiveAttributes = options.valuesOf(seOption);
            final List<String> insensitiveAttributes = options.valuesOf(isOption);
            final List<String> identifyingAttributes = options.valuesOf(idOption);

            // define qis
            for (final String attributName : quasiIdentifier) {
                if (!hierarchies.containsKey(attributName)) {
                    throw new IllegalArgumentException("quasi identifiers must have a hierarchy specified: " + attributName);
                }
                data.getDefinition().setAttributeType(attributName, hierarchies.get(attributName));
            }

            // define ses
            for (final String attributName : sensitiveAttributes) {
                data.getDefinition().setAttributeType(attributName, org.deidentifier.arx.AttributeType.SENSITIVE_ATTRIBUTE);
            }

            // define is
            for (final String attributName : insensitiveAttributes) {
                data.getDefinition().setAttributeType(attributName, org.deidentifier.arx.AttributeType.INSENSITIVE_ATTRIBUTE);
            }

            // define id
            for (final String attributName : identifyingAttributes) {
                data.getDefinition().setAttributeType(attributName, org.deidentifier.arx.AttributeType.IDENTIFYING_ATTRIBUTE);
            }

            // data types
            final Map<String, DataType<?>> dataTypes = parseDataTypes(options.valuesOf(dataTypeOption));
            for (final Entry<String, DataType<?>> entry : dataTypes.entrySet()) {
                data.getDefinition().setDataType(entry.getKey(), entry.getValue());
            }

            // build config
            final ARXConfiguration config = ARXConfiguration.create();
            config.setMaxOutliers(supression);
            config.setPracticalMonotonicity(practicalMonotonicity);
            config.setMetric(metric);

            // set criteria
            for (final PrivacyCriterion criterion : criteria) {
                config.addCriterion(criterion);
            }

            if (output != null) {
                System.out.println("Using the following criteria for anonymization: " + criteria);
            }

            final ARXAnonymizer anonymizer = new ARXAnonymizer();
            final ARXResult result = anonymizer.anonymize(data, config);

            if (output != null) { // save to file
                result.getOutput().save(output, separator);
            } else { // output on console
                final Iterator<String[]> transformed = result.getOutput().iterator();
                while (transformed.hasNext()) {
                    final String[] line = transformed.next();
                    final StringBuilder outline = new StringBuilder();
                    for (int i = 0; i < line.length; i++) {
                        outline.append(line[i]);
                        if (i < (line.length - 1)) {
                            outline.append(separator);
                        }
                    }
                    // outline.append("\n");
                    System.out.println(outline);
                }
            }

        } catch (final Exception e) {
            try {
                System.err.println(e.getLocalizedMessage());
                e.printStackTrace(); // TODO: for debugging only
                parser.printHelpOn(System.out);
                System.exit(1);
            } catch (final IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
        }

    }
}
