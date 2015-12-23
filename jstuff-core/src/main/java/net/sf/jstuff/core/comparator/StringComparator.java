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
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 1L;

    // collator is only serializable starting Java 6 
    private transient Collator collator;
    private final Locale locale;

    public StringComparator() {
        this(Locale.getDefault());
    }

    public StringComparator(final Locale locale) {
        this.locale = locale;
    }

    private Collator _getCollator() {
        if (collator == null)
            collator = Collator.getInstance(locale);
        return collator;
    }

    public int compare(final String o1, final String o2) {
        if (o1 == o2)
            return 0;
        return _getCollator().compare(o1, o2);
    }

    public Locale getLocale() {
        return locale;
    }
}