/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesMixinTest extends TestCase {
   protected interface TestEntity {
      String createGreeting(String name);

      String createClosing(String name);
   }

   protected static class TestEntityImpl implements TestEntity {
      @Override
      public String createClosing(final String name) {
         return "Goodbye " + name + ".";
      }

      @Override
      public String createGreeting(final String name) {
         return "Hello " + name + "!";
      }
   }

   public void testMixin() {
      final TestEntityImpl delegate = new TestEntityImpl();
      final TestEntity proxy = Types.createMixin(TestEntity.class, new Object() {
         @SuppressWarnings("unused")
         public String createGreeting(final String name) {
            return delegate.createGreeting(name) + " How are you?";
         }
      }, delegate);
      assertEquals("Hello John! How are you?", proxy.createGreeting("John"));
      assertEquals("Goodbye John.", proxy.createClosing("John"));
   }
}
