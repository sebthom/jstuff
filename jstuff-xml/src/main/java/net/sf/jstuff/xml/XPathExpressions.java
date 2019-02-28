/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
