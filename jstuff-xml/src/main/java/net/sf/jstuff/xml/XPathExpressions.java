/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.xml;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class XPathExpressions {
    /**
     * Generates an XPath 1 compatible ends-with expression
     *
     * XPath 2.0: //*[ends-with(name(),'-configuration')]
     * XPath 1.0: //*[substring(name(), string-length(name()) - 14 + 1) = '-configuration']
     *
     * @param text e.g. "name()", "."
     * @param suffix
     */
    public static String endsWith(final String text, final String suffix) {
        return "substring(" + text + ", string-length(" + text + ") - " + suffix.length() + " + 1) = '" + suffix + "'";
    }
}
