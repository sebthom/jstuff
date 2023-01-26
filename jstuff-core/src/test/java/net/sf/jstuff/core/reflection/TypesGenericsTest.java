/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.reflection.Types.*;
import static org.apache.commons.lang3.ArrayUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesGenericsTest {
   public static class ClassA_1 {
   }

   public static class ClassA_2<T> extends ClassA_1 implements InterfaceA_5<T> {
   }

   public static class ClassA_3<T1, T2> extends ClassA_2<T1> implements InterfaceB<T1, T2> {
   }

   public static class ClassA_4 extends ClassA_3<String, Integer> implements InterfaceC {
   }

   public static class ClassA_5 extends ClassA_4 {
   }

   public static class ClassB<T> {
   }

   public interface InterfaceA_1 {
   }

   public interface InterfaceA_2<T> extends InterfaceA_1 {
   }

   public interface InterfaceA_3 extends InterfaceA_2<Double> {
   }

   public interface InterfaceA_4<T> extends InterfaceA_3 {
   }

   public interface InterfaceA_5<T> extends InterfaceA_4<T> {
   }

   public interface InterfaceB<T, T2> {
   }

   public interface InterfaceC {
   }

   public <T> void assertEquals(final List<T> actual, @SuppressWarnings("unchecked") final T... expected) {
      assertThat(Arrays.asList(expected)).isEqualTo(new ArrayList<>(actual));
   }

   @Test
   @SuppressWarnings({"unchecked", "rawtypes"})
   public void testGenerics() {
      // CLASSES: good behavior tests
      assertThat(findGenericTypeArguments(ClassA_1.class, ClassA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_2.class, ClassA_2.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(ClassA_2.class, ClassA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_3.class, ClassA_3.class)).isEqualTo(toArray(null, null));
      assertThat(findGenericTypeArguments(ClassA_3.class, ClassA_2.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(ClassA_3.class, ClassA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_4.class, ClassA_4.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_4.class, ClassA_3.class)).isEqualTo(toArray(String.class, Integer.class));
      assertThat(findGenericTypeArguments(ClassA_4.class, ClassA_2.class)).isEqualTo(toArray(String.class));
      assertThat(findGenericTypeArguments(ClassA_4.class, ClassA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_5.class, ClassA_5.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_5.class, ClassA_4.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_5.class, ClassA_3.class)).isEqualTo(toArray(String.class, Integer.class));
      assertThat(findGenericTypeArguments(ClassA_5.class, ClassA_2.class)).isEqualTo(toArray(String.class));
      assertThat(findGenericTypeArguments(ClassA_5.class, ClassA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      // CLASSES: bad behavior tests
      assertThat(findGenericTypeArguments(Long.class, Number.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(Byte.class, byte.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(byte.class, Byte.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(byte.class, Number.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      try {
         findGenericTypeArguments((Class) ClassA_5.class, (Class) ClassB.class);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) { /* expected */ }

      // INTERFACES: good behavior tests
      assertThat(findGenericTypeArguments(ClassA_2.class, InterfaceA_5.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(ClassA_2.class, InterfaceA_4.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(ClassA_2.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_2.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(ClassA_2.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_3.class, InterfaceA_5.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(ClassA_3.class, InterfaceA_4.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(ClassA_3.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_3.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(ClassA_3.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceA_5.class)).isEqualTo(toArray(String.class));
      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceA_4.class)).isEqualTo(toArray(String.class));
      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceA_5.class)).isEqualTo(toArray(String.class));
      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceA_4.class)).isEqualTo(toArray(String.class));
      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(ClassA_3.class, InterfaceB.class)).isEqualTo(toArray(null, null));
      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceB.class)).isEqualTo(toArray(String.class, Integer.class));
      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceB.class)).isEqualTo(toArray(String.class, Integer.class));

      assertThat(findGenericTypeArguments(ClassA_4.class, InterfaceC.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(ClassA_5.class, InterfaceC.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(InterfaceA_1.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(InterfaceA_2.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(InterfaceA_2.class, InterfaceA_2.class)).isEqualTo(toArray((Class<?>) null));

      assertThat(findGenericTypeArguments(InterfaceA_3.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(InterfaceA_3.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(InterfaceA_3.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);

      assertThat(findGenericTypeArguments(InterfaceA_4.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(InterfaceA_4.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(InterfaceA_4.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(InterfaceA_4.class, InterfaceA_4.class)).isEqualTo(toArray((Class<?>) null));

      assertThat(findGenericTypeArguments(InterfaceA_5.class, InterfaceA_1.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(InterfaceA_5.class, InterfaceA_2.class)).isEqualTo(toArray(Double.class));
      assertThat(findGenericTypeArguments(InterfaceA_5.class, InterfaceA_3.class)).isEqualTo(EMPTY_CLASS_ARRAY);
      assertThat(findGenericTypeArguments(InterfaceA_5.class, InterfaceA_4.class)).isEqualTo(toArray((Class<?>) null));
      assertThat(findGenericTypeArguments(InterfaceA_5.class, InterfaceA_5.class)).isEqualTo(toArray((Class<?>) null));
   }
}
