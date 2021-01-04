/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FieldsTest {

   static class Entity {
      final String name;

      Entity(final String name) {
         this.name = name;
      }
   }

   @Test
   public void testWriteIgnoringFinal() {
      final Entity e = new Entity("foo");
      assertThat(e.name).isEqualTo("foo");

      Fields.writeIgnoringFinal(e, "name", "bar");
      assertThat(e.name).isEqualTo("bar");
   }
}
