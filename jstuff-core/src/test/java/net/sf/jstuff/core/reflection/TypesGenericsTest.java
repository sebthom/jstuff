/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.reflection.Types.findGenericTypeArguments;
import static net.sf.jstuff.core.reflection.Types.isAssignableTo;
import static net.sf.jstuff.core.reflection.Types.resolveUnderlyingClass;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_CLASS_ARRAY;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.validation.Args;

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

   /**
    * old implementation without using visitor - slightly faster but more code and harder to read
    */
   @SuppressWarnings({"null"})
   private static <T> Class<?>[] _findGenericTypeArguments(final Class<? extends T> searchIn, final Class<T> searchFor) {
      Args.notNull("searchIn", searchIn);
      Args.notNull("searchFor", searchFor);

      // if the searchFor type is not a generic type there is nothing to find
      if (searchFor.getTypeParameters().length == 0)
         return ArrayUtils.EMPTY_CLASS_ARRAY;

      if (!searchFor.isAssignableFrom(searchIn))
         throw new IllegalArgumentException("Class [searchIn=" + searchIn.getName() + "] is assignable to [searchFor=" + searchFor.getName() + "]");

      final boolean isSearchForInterface = searchFor.isInterface();
      final Queue<Type> interfacesToCheck = isSearchForInterface ? new LinkedList<>() : null;

      /*
       * traverse the class hierarchy and collect generic variable => concrete variable argument (type) mappings
       */
      final Map<TypeVariable<?>, Type> genericVariableToArgumentMappings = Maps.newHashMap();
      Type currentType = searchIn;
      outer: while (true) {
         if (currentType == Object.class) {
            break;
         }

         final Class<?> currentClass = resolveUnderlyingClass(currentType);

         // populate the mappings with info from generic class
         if (currentType instanceof ParameterizedType) {
            Maps.putAll(genericVariableToArgumentMappings, //
               /*generic variable*/(TypeVariable<?>[]) currentClass.getTypeParameters(), //
               /*arguments (concrete types) of generic variables*/((ParameterizedType) currentType).getActualTypeArguments() //
            );
         }

         if (currentClass == searchFor) {
            break;
         }

         if (isSearchForInterface) {
            for (final Type ifaceType : currentClass.getGenericInterfaces()) {
               if (!isAssignableTo(resolveUnderlyingClass(ifaceType), searchFor)) {
                  continue;
               }
               interfacesToCheck.add(ifaceType);
            }

            while (!interfacesToCheck.isEmpty()) {
               final Type currentInterfaceType = interfacesToCheck.poll();
               if (currentInterfaceType == Object.class) {
                  continue;
               }

               final Class<?> currentInterfaceClass = resolveUnderlyingClass(currentInterfaceType);

               // populate the mappings with info from generic interfaces
               if (currentInterfaceType instanceof ParameterizedType) {
                  Maps.putAll(genericVariableToArgumentMappings, //
                     /*generic variable*/(TypeVariable<?>[]) currentInterfaceClass.getTypeParameters(), //
                     /*arguments (concrete types) of generic variables*/((ParameterizedType) currentInterfaceType).getActualTypeArguments() //
                  );
               }

               if (currentInterfaceClass == searchFor) {
                  currentType = currentInterfaceType;
                  break outer;
               }

               for (final Type ifaceType : currentInterfaceClass.getGenericInterfaces()) {
                  if (!isAssignableTo(resolveUnderlyingClass(ifaceType), searchFor)) {
                     continue;
                  }
                  interfacesToCheck.add(ifaceType);
               }
            }
         }
         currentType = currentClass.getGenericSuperclass();
      }

      /*
       * build the result list based on the information collected in genericVariableToTypeMappings
       */
      final Type[] genericVariables;
      if (currentType instanceof Class) {
         genericVariables = ((Class<?>) currentType).getTypeParameters();
      } else {
         genericVariables = ((ParameterizedType) currentType).getActualTypeArguments();
      }
      final Class<?>[] res = new Class<?>[genericVariables.length];
      for (int i = 0, l = genericVariables.length; i < l; i++) {
         Type genericVariable = genericVariables[i];
         while (genericVariableToArgumentMappings.containsKey(genericVariable)) {
            genericVariable = genericVariableToArgumentMappings.get(genericVariable);
         }
         res[i] = resolveUnderlyingClass(genericVariable);
      }
      return res;
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

   @Test
   public void testPerformance() {
      final int iterations = 10000;
      final StopWatch sw = new StopWatch();
      sw.start();
      for (int i = 0; i < iterations; i++) {
         _findGenericTypeArguments(ClassA_5.class, InterfaceA_2.class);
      }
      sw.stop();
      System.out.println(sw.toString());

      sw.reset();
      sw.start();
      for (int i = 0; i < iterations; i++) {
         findGenericTypeArguments(ClassA_5.class, InterfaceA_2.class);
      }
      sw.stop();
      System.out.println(sw.toString());

      sw.reset();
      sw.start();
      for (int i = 0; i < iterations; i++) {
         _findGenericTypeArguments(ClassA_5.class, InterfaceA_2.class);
      }
      sw.stop();
      System.out.println(sw.toString());

      sw.reset();
      sw.start();
      for (int i = 0; i < iterations; i++) {
         findGenericTypeArguments(ClassA_5.class, InterfaceA_2.class);
      }
      sw.stop();

      System.out.println(sw.toString());
   }
}
