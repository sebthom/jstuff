/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.feed;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
