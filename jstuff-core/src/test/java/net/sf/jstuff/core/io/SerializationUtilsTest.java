/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.*;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SerializationUtilsTest {

   public static final class Entity {
      @Nullable
      private String name;

      public Entity() {
      }

      public Entity(final String name) {
         this.name = name;
      }

      @Override
      public boolean equals(@Nullable final Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         final Entity other = (Entity) obj;
         if (!Objects.equals(name, other.name))
            return false;
         return true;
      }

      @Nullable
      public String getName() {
         return name;
      }

      @Override
      public int hashCode() {
         return Objects.hash(name);
      }

      public void setName(@Nullable final String name) {
         this.name = name;
      }
   }

   @Test
   public void testBean2XML() {
      final Entity bean = new Entity("foobar");
      System.out.println(SerializationUtils.bean2xml(bean));
      final Entity clone = SerializationUtils.xml2bean(SerializationUtils.bean2xml(bean));
      assertThat(clone) //
         .isNotSameAs(bean) //
         .isEqualTo(bean);
   }
}
