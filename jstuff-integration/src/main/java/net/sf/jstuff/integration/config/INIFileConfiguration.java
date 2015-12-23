/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.config;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.functional.Function;
import net.sf.jstuff.core.functional.Functions;

/**
 * An INI File Configuration implementation that preserves the order of comments and sections on save
 * and supports auto sorting of sections and properties.
 *
 * Supported comment characters are: <code>#</code> and <code>;</code>
 * Supported value assignment character is <code>=</code>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class INIFileConfiguration extends AbstractFileConfiguration {

    private final Set<String> loadedSections = new LinkedHashSet<String>();
    private final Map<String, List<String>> comments = new HashMap<String, List<String>>();
    private final Map<String, String> inlineComments = new HashMap<String, String>();

    private boolean isAutoSort = true;

    /**
     * Create a new empty INI configuration.
     */
    public INIFileConfiguration() {
        super();
    }

    /**
     * Create and load the INI configuration from the given file.
     *
     * @param file The INI file to load.
     * @throws ConfigurationException If an error occurs while loading the file
     */
    public INIFileConfiguration(final File file) throws ConfigurationException {
        super(file);
    }

    /**
     * Create and load the INI configuration from the given file.
     *
     * @param fileName The name or path of the INI file to load.
     * @throws ConfigurationException If an error occurs while loading the file
     */
    public INIFileConfiguration(final String fileName) throws ConfigurationException {
        super(fileName);
    }

    /**
     * Create and load the INI configuration from the given URL.
     *
     * @param url The URL of the INI file to load.
     * @throws ConfigurationException If an error occurs while loading the file
     */
    public INIFileConfiguration(final URL url) throws ConfigurationException {
        super(url);
    }

    /**
     * @return the comments for the given section / property
     */
    public String[] getComments(final String key) {
        return ArrayUtils.toArray(transform(comments.get(key), new Function<String, String>() {
            public String apply(final String source) {
                // strip the comment character
                return source.startsWith("#") || source.startsWith(";") ? "" : source.substring(1);
            }
        }), String.class);
    }

    /**
     * @return the inline comment for the given property
     */
    public String getInlineComment(final String key) {
        return inlineComments.get(key);
    }

    @Override
    public String getProperty(final String key) {
        return StringUtils.toString(super.getProperty(key));
    }

    public String getPropertyNonInterpolated(final String key) {
        return StringUtils.toString(resolveContainerStore(key));
    }

    public Set<String> getSections() {
        final Set<String> sections = isAutoSort ? new TreeSet<String>() : new LinkedHashSet<String>();
        sections.addAll(loadedSections);

        // this will result in new sections being added to the end of the file
        for (final String key : toIterable(getKeys())) {
            final int index = key.indexOf('.');
            if (index > -1) {
                sections.add(key.substring(0, index - 1));
            }
        }
        return sections;
    }

    public boolean isAutoSort() {
        return isAutoSort;
    }

    protected boolean isCommentLine(final String line) {
        if (line == null)
            return false;

        // empty lines are treated as comments
        if (line.length() == 0)
            return true;

        return StringUtils.startsWith(line, '#') || StringUtils.startsWith(line, ';');
    }

    protected boolean isSectionLine(final String line) {
        return StringUtils.startsWith(line, '[') && StringUtils.endsWith(line, ']');
    }

    public void load(final Reader in) throws ConfigurationException {
        final BufferedReader input = new BufferedReader(in);

        String currentSection = "";
        String currentCommentKey = "";

        String line;

        try {
            while ((line = input.readLine()) != null) {
                line = line.trim();

                // process comments
                if (isCommentLine(line)) {
                    if (comments.containsKey(currentCommentKey)) {
                        comments.get(currentCommentKey).add(line);
                    } else {
                        comments.put(currentCommentKey, newArrayList(line));
                    }
                } else if (isSectionLine(line)) {
                    currentSection = line.substring(1, line.length() - 2);
                    currentCommentKey = currentSection;

                    // remember section order
                    loadedSections.add(currentSection);
                } else {
                    final String key;
                    final String value;
                    String inlineComment = null;
                    // check if the line contains a property assignment using key=value, if not we define the line whole line is the key and the value is empty ("")
                    final int index = line.indexOf('=');
                    if (index > -1) {
                        final Tuple2<String, String> v = parseValueLine(line.substring(index + 1));
                        key = currentSection + '.' + line.substring(0, index).trim();
                        value = v.get1();
                        inlineComment = v.get2();
                    } else {
                        final Tuple2<String, String> v = parseValueLine(line);
                        key = currentSection + '.' + v.get1();
                        value = "";
                        inlineComment = v.get2();
                    }
                    currentCommentKey = key;
                    setProperty(key, value, inlineComment);
                }
            }
        } catch (final IOException ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * @return Tuple<value, inlineComment>
     */
    protected Tuple2<String, String> parseValueLine(String value) {
        value = value.trim();
        final int valueLen = value.length();
        if (valueLen == 0)
            return Tuple2.create("", null);

        final boolean isQuoted = value.charAt(0) == '"' || value.charAt(0) == '\'';
        boolean isEscaped = false;

        char quote = 0;
        int charPos = 0;
        if (isQuoted) {
            quote = value.charAt(0);
            charPos = 1;
        }

        String inlineComment = null;

        final StringBuilder result = new StringBuilder();
        while (charPos < valueLen) {
            final char c = value.charAt(charPos);

            if (isQuoted) {
                if (c == '\\' && !isEscaped) {
                    isEscaped = true;
                } else if (!isEscaped && quote == c) {
                    break;
                } else if (isEscaped && quote == c) {
                    isEscaped = false;
                    result.append(c);
                } else {
                    if (isEscaped) {
                        isEscaped = false;
                        result.append('\\');
                    }
                    result.append(c);
                }
            } else if (c == '#' || c == ';') {
                inlineComment = value.substring(charPos + 1).trim();
                break;
            } else {
                result.append(c);
            }

            charPos++;
        }
        return Tuple2.create(isQuoted ? result.toString() : result.toString().trim(), inlineComment);
    }

    public void save(final Writer out) throws ConfigurationException {
        final BufferedWriter bw = new BufferedWriter(out);

        try {
            final List<String> headerComments = comments.get("");
            if (headerComments != null) {
                for (final String headerComment : headerComments) {
                    bw.write(headerComment);
                    bw.newLine();
                }
            }

            // process all sections
            for (final String section : getSections()) {
                if (isAutoSort) {
                    bw.newLine();
                }

                bw.write('[');
                bw.write(section);
                bw.write(']');
                bw.newLine();

                final List<String> sectionComments = comments.get(section);
                if (sectionComments != null) {
                    for (final String sectionComment : sectionComments) {
                        if (isAutoSort && sectionComment.length() == 0) {
                            continue;
                        }
                        bw.write(sectionComment);
                        bw.newLine();
                    }
                }

                final Configuration subset = subset(section);

                final List<String> keys = toList(subset.getKeys());
                if (isAutoSort) {
                    Collections.sort(keys);
                }

                for (final String key : keys) {
                    final String fqName = section + "." + key;
                    final String value = (String) subset.getProperty(key);
                    bw.write(key);
                    bw.write('=');
                    if (value.indexOf("#") > -1 || value.indexOf(";") > -1 || value.indexOf(" ") > -1) {
                        // add quotes around the specified value if it contains a comment character
                        bw.write('"');
                        bw.write(StringUtils.replace(value, "\"", "\\\""));
                        bw.write('"');
                    } else {
                        bw.write(value);
                    }
                    final String inlineComment = inlineComments.get(fqName);
                    if (inlineComment != null) {
                        bw.write(" # ");
                        bw.write(inlineComment);
                    }
                    bw.newLine();

                    final List<String> propertyComments = comments.get(fqName);
                    if (propertyComments != null) {
                        for (final String propertyComment : propertyComments) {
                            if (isAutoSort && propertyComment.length() == 0) {
                                continue;
                            }
                            bw.write(propertyComment);
                            bw.newLine();
                        }
                    }
                }
            }
        } catch (final IOException ex) {
            throw new ConfigurationException(ex);
        }
    }

    public void setAutoSort(final boolean isAutoSort) {
        this.isAutoSort = isAutoSort;
    }

    /**
     * @param comments the comments for the given section / property
     */
    public void setComments(final String key, final String... comments) {
        if (comments == null) {
            this.comments.remove(key);
        } else {
            this.comments.put(key, transform(newArrayList(comments), Functions.prefix("#")));
        }
    }

    public void setProperty(final String key, final String value, final String inlineComment) {
        super.setProperty(key, value);
        if (inlineComment != null) {
            inlineComments.put(key, inlineComment.trim());
        } else {
            inlineComments.remove(key);
        }
    }
}
