/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomCategory {
   private String term;

   public AtomCategory() {
   }

   public AtomCategory(final String term) {
      this.term = term;
   }

   public String getTerm() {
      return term;
   }

   public void setTerm(final String term) {
      this.term = term;
   }
}
