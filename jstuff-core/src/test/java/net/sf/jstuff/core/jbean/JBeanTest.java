/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.jbean;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.Test;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.jbean.changelog.PropertyChangelog;
import net.sf.jstuff.core.jbean.changelog.UndoMarker;
import net.sf.jstuff.core.jbean.meta.ClassDescriptor;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class JBeanTest {

   private static final class MyEntity extends AbstractJBean {
      private static final long serialVersionUID = 1L;

      public static final ClassDescriptor<MyEntity> META = ClassDescriptor.of(MyEntity.class, "MyEntity", "", null);

      // CHECKSTYLE:IGNORE ConstantName FOR NEXT 2 LINES
      public static final PropertyDescriptor<String> PROP_comment = PropertyDescriptor.create(META, //
         "comment", String.class, 0, 1, false, false, true, //
         "the entity's comment", Maps.newHashMap( //
            "length", 64, //
            "min-length", 0 //
         ));

      private String comment;

      @Override
      @SuppressWarnings("unchecked")
      public <T> T _get(final PropertyDescriptor<T> property) {
         Args.notNull("property", property);
         if (property == PROP_comment)
            return (T) getComment();
         return super._get(property);
      }

      @Override
      public ClassDescriptor<?> _getMeta() {
         return META;
      }

      @Override
      public <T> MyEntity _set(final PropertyDescriptor<T> property, final T value) {
         Args.notNull("property", property);
         if (property == PROP_comment) {
            setComment((String) value);
         } else {
            super._set(property, value);
         }
         return this;
      }

      public String getComment() {
         return comment;
      }

      public void setComment(String newValue) {
         newValue = newValue == null ? null : newValue.trim();
         final String oldValue = getComment();
         if (!Objects.equals(oldValue, newValue)) {
            comment = newValue;
            onValueSet(PROP_comment, oldValue, newValue);
         }
      }
   }

   @Test
   public void testJBean() {
      final PropertyChangelog changeLog = new PropertyChangelog();

      final MyEntity entity = new MyEntity();
      entity._subscribe(changeLog);

      assertThat(changeLog.isDirty(entity)).isFalse();

      entity.setComment("STEP1");

      assertThat(changeLog.isDirty(entity)).isTrue();

      final UndoMarker marker = changeLog.undoMarker();

      entity.setComment("STEP2");
      assertThat(entity.getComment()).isEqualTo("STEP2");

      changeLog.undo(marker);
      assertThat(entity.getComment()).isEqualTo("STEP1");
      assertThat(changeLog.isDirty(entity)).isTrue();

      changeLog.undo();
      assertThat(changeLog.isDirty(entity)).isFalse();
   }
}
