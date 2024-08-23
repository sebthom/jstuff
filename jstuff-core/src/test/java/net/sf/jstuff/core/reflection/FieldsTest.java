/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FieldsTest {

   static class Entity {
      @Nullable
      private static final Entity INSTANCE = null;
      private final String name;

      Entity(final String name) {
         this.name = name;
      }
   }

   public void testAccessFieldModifiers() {
      // Field.class.getDeclaredField("modifiers") throws NoSuchFieldException on JDK 12+
      // this is to ensure we can still access it
      assertThat(Fields.find(Field.class, "modifiers")).isNotNull();

      assertThat(Fields.findVarHandle(Field.class, "modifiers")).isNotNull();
   }

   @Test
   public void testWriteIgnoringFinal() throws SecurityException {
      // instance field test
      final var e = new Entity("foo");
      assertThat(e.name).isEqualTo("foo");

      Fields.writeIgnoringFinal(e, "name", "bar");
      assertThat(e.name).isEqualTo("bar");

      // static final field test
      Fields.writeIgnoringFinal(Entity.class, "INSTANCE", e);
      assertThat(Entity.INSTANCE).isEqualTo(e);
   }
}
