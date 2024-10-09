/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.meta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.jbean.meta.ClassDescriptor;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class MetaSerializationTest {
   static class Entity<SELF_TYPE extends Entity<SELF_TYPE>> {
      static final ClassDescriptor<EntityMeta> META_CLASS = EntityMeta.META_CLASS;

      private String comment;

      @SuppressWarnings("unchecked")
      <T> T _get(final PropertyDescriptor<T> property) {
         Args.notNull("property", property);

         if (property.equals(EntityMeta.PROP_comment))
            return (T) getComment();
         throw new UnsupportedOperationException();
      }

      ClassDescriptor<EntityMeta> _getMetaClass() {
         return EntityMeta.META_CLASS;
      }

      <T> SELF_TYPE _set(final PropertyDescriptor<T> property, final T value) {
         Args.notNull("property", property);

         if (property.equals(EntityMeta.PROP_comment)) {
            setComment((String) value);
         }
         throw new UnsupportedOperationException();
      }

      String getComment() {
         return comment;
      }

      void setComment(final String comment) {
         this.comment = comment;
      }
   }

   static class EntityMeta {
      @NonNull
      static final ClassDescriptor<EntityMeta> META_CLASS = ClassDescriptor.of(EntityMeta.class, "Entity", null, null);

      // CHECKSTYLE:IGNORE ConstantName FOR NEXT 2 LINES
      static final PropertyDescriptor<String> PROP_comment = PropertyDescriptor.create(META_CLASS, //
         "comment", String.class, 0, 1, false, false, true, //
         "", Maps.newHashMap( //
            "descr", (Serializable) "this entity's comment" //
         ));
   }

   @Test
   void testSerialization() {
      assertThat(Entity.META_CLASS).isNotNull();
      assertThat(SerializationUtils.clone(Entity.META_CLASS)).isSameAs(Entity.META_CLASS);
      assertThat(SerializationUtils.clone(EntityMeta.PROP_comment.getMetaClass())).isSameAs(Entity.META_CLASS);
      assertThat(SerializationUtils.clone(EntityMeta.PROP_comment)).isSameAs(EntityMeta.PROP_comment);
   }
}
