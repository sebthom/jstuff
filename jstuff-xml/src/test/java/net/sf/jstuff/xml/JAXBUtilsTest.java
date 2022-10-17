/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class JAXBUtilsTest {

   public static class MyEntity {
      private @Nullable MyEntity child;
      private @Nullable String name;

      public @Nullable MyEntity getChild() {
         return child;
      }

      public @Nullable String getName() {
         return name;
      }

      public void setChild(final MyEntity child) {
         this.child = child;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   @Test
   public void testToXML() {
      final var e = new MyEntity();
      e.name = "a";
      final var child = new MyEntity();
      child.name = "b";
      e.child = child;
      System.out.println(JAXBUtils.toXML(e));
   }

   @Test
   public void testToXSD() {
      System.out.println(JAXBUtils.toXSD(MyEntity.class));
   }
}
