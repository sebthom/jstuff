/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.persistence.jpa;

import javax.persistence.Query;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class JPAUtils {
   /**
    * Changes question marks in a query string to positional question marks.
    *
    * E.g. "?, ?, ?" becomes "?1, ?2, ?3"
    */
   public static String enumerateQuestionMarks(final String queryString) {
      if (queryString == null || queryString.length() == 0)
         return queryString;

      final StringBuilder out = new StringBuilder();
      int startSearchAt = 0, foundAt = 0;

      for (int i = 1; (foundAt = queryString.indexOf("?", startSearchAt)) >= 0; i++) {
         final String replaceValue = "?" + i;
         out.append(queryString, startSearchAt, foundAt).append(replaceValue);
         startSearchAt = foundAt + 1;
      }
      return out.append(queryString, startSearchAt, queryString.length()).toString();
   }

   public static void setParameters(final Query query, final Object... parameters) {
      if (parameters != null) {
         for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
         }
      }
   }
}
