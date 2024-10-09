/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class TypesMixinTest {
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
   void testMixin() {
      final var delegate = new TestEntityImpl();
      final TestEntity proxy = Types.createMixin(TestEntity.class, new Object() {
         @SuppressWarnings("unused")
         String createGreeting(final String name) {
            return delegate.createGreeting(name) + " How are you?";
         }
      }, delegate);
      assertThat(proxy.createGreeting("John")).isEqualTo("Hello John! How are you?");
      assertThat(proxy.createClosing("John")).isEqualTo("Goodbye John.");
   }
}
