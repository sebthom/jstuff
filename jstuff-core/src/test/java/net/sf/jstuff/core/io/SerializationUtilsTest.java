/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SerializationUtilsTest {

   public static final class Entity {
      private String name;

      public Entity() {
      }

      public Entity(final String name) {
         this.name = name;
      }

      @Override
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         final Entity other = (Entity) obj;
         if (!Objects.equals(name, other.name))
            return false;
         return true;
      }

      public String getName() {
         return name;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (name == null ? 0 : name.hashCode());
         return result;
      }

      public void setName(final String name) {
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
