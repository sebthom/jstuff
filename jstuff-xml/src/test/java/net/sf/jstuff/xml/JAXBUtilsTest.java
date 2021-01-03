/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class JAXBUtilsTest {

   public static class MyEntity {
      private MyEntity child;
      private String name;

      public MyEntity getChild() {
         return child;
      }

      public String getName() {
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
      final MyEntity e = new MyEntity();
      e.name = "a";
      e.child = new MyEntity();
      e.child.name = "b";
      System.out.println(JAXBUtils.toXML(e));
   }

   @Test
   public void testToXSD() {
      System.out.println(JAXBUtils.toXSD(MyEntity.class));
   }
}
