/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class XPathExpressions {
   /**
    * Generates an XPath 1 compatible ends-with expression.
    *
    * <pre>
    * XPath 2.0: //*[ends-with(name(),'-configuration')]
    * XPath 1.0: //*[substring(name(), string-length(name()) - 14 + 1) = '-configuration']
    * </pre>
    *
    * @param text e.g. "name()", "."
    */
   public static String endsWith(final String text, final String suffix) {
      return "substring(" + text + ", string-length(" + text + ") - " + suffix.length() + " + 1) = '" + suffix + "'";
   }
}
