/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class SerializationUtilsTest {

   public static final class Entity {
      private @Nullable String name;

      public Entity() { // CHECKSTYLE:IGNORE .*
      }

      public Entity(final String name) { // CHECKSTYLE:IGNORE .*
         this.name = name;
      }

      @Override
      public boolean equals(final @Nullable Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         final Entity other = (Entity) obj;
         if (!Objects.equals(name, other.name))
            return false;
         return true;
      }

      public @Nullable String getName() {
         return name;
      }

      @Override
      public int hashCode() {
         return Objects.hash(name);
      }

      public void setName(final @Nullable String name) {
         this.name = name;
      }
   }

   @Test
   void testBean2XML() {
      final var bean = new Entity("foobar");
      System.out.println(SerializationUtils.bean2xml(bean));
      final Entity clone = SerializationUtils.xml2bean(SerializationUtils.bean2xml(bean));
      assertThat(clone) //
         .isNotSameAs(bean) //
         .isEqualTo(bean);
   }
}
