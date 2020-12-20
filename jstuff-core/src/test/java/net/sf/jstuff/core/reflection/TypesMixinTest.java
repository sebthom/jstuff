/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesMixinTest {
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

   @Test
   public void testMixin() {
      final TestEntityImpl delegate = new TestEntityImpl();
      final TestEntity proxy = Types.createMixin(TestEntity.class, new Object() {
         @SuppressWarnings("unused")
         public String createGreeting(final String name) {
            return delegate.createGreeting(name) + " How are you?";
         }
      }, delegate);
      assertThat(proxy.createGreeting("John")).isEqualTo("Hello John! How are you?");
      assertThat(proxy.createClosing("John")).isEqualTo("Goodbye John.");
   }
}
