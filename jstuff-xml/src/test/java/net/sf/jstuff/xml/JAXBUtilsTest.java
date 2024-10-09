/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class JAXBUtilsTest {

   static class MyEntity {
      private @Nullable MyEntity child;
      private @Nullable String name;

      @Nullable
      MyEntity getChild() {
         return child;
      }

      @Nullable
      String getName() {
         return name;
      }

      void setChild(final MyEntity child) {
         this.child = child;
      }

      void setName(final String name) {
         this.name = name;
      }
   }

   @Test
   void testToXML() {
      final var e = new MyEntity();
      e.name = "a";
      final var child = new MyEntity();
      child.name = "b";
      e.child = child;
      System.out.println(JAXBUtils.toXML(e));
   }

   @Test
   void testToXSD() {
      System.out.println(JAXBUtils.toXSD(MyEntity.class));
   }
}
