/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.meta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;

import org.junit.Test;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.jbean.meta.ClassDescriptor;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MetaSerializationTest {
   public static class Entity<SELF_TYPE extends Entity<SELF_TYPE>> {
      public static final ClassDescriptor<EntityMeta> META_CLASS = EntityMeta.META_CLASS;

      private String comment;

      @SuppressWarnings("unchecked")
      public <T> T _get(final PropertyDescriptor<T> property) {
         Args.notNull("property", property);

         if (property.equals(EntityMeta.PROP_comment))
            return (T) getComment();
         throw new UnsupportedOperationException();
      }

      public ClassDescriptor<EntityMeta> _getMetaClass() {
         return EntityMeta.META_CLASS;
      }

      public <T> SELF_TYPE _set(final PropertyDescriptor<T> property, final T value) {
         Args.notNull("property", property);

         if (property.equals(EntityMeta.PROP_comment)) {
            setComment((String) value);
         }
         throw new UnsupportedOperationException();
      }

      public String getComment() {
         return comment;
      }

      public void setComment(final String comment) {
         this.comment = comment;
      }
   }

   public static class EntityMeta {
      public static final ClassDescriptor<EntityMeta> META_CLASS = ClassDescriptor.of(EntityMeta.class, "Entity", null, null);

      // CHECKSTYLE:IGNORE ConstantName FOR NEXT 2 LINES
      public static final PropertyDescriptor<String> PROP_comment = PropertyDescriptor.create(META_CLASS, //
         "comment", String.class, 0, 1, false, false, true, //
         "", Maps.newHashMap( //
            "descr", (Serializable) "this entity's comment" //
         ));
   }

   @Test
   public void testSerialization() {
      assertThat(Entity.META_CLASS).isNotNull();
      assertThat(SerializationUtils.clone(Entity.META_CLASS)).isSameAs(Entity.META_CLASS);
      assertThat(SerializationUtils.clone(EntityMeta.PROP_comment.getMetaClass())).isSameAs(Entity.META_CLASS);
      assertThat(SerializationUtils.clone(EntityMeta.PROP_comment)).isSameAs(EntityMeta.PROP_comment);
   }
}
