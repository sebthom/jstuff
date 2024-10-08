/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.reflection.MethodsTestEntities.EntityA;
import net.sf.jstuff.core.reflection.MethodsTestEntities.EntityB;
import net.sf.jstuff.core.reflection.exception.ReflectionException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class MethodsTest {

   @FunctionalInterface
   interface EntityB_getProperty2_Accessor {
      int invoke(EntityB entity);
   }

   @FunctionalInterface
   interface EntityB_isProperty3_Accessor {
      boolean invoke(EntityB entity);
   }

   @FunctionalInterface
   interface EntityB_setProperty3_Accessor {
      void invoke(EntityB entity, boolean propertyValue);
   }

   @Test
   void test_createPublicGetterAccessor() {
      final var entity = new EntityB();
      entity.setProperty3(true);

      try {
         Methods.createPublicGetterAccessor(EntityB.class, "property2", int.class).invoke(entity);
         failBecauseExceptionWasNotThrown(ReflectionException.class);
      } catch (final ReflectionException ex) {
         assertThat(asNonNull(ex.getCause()).getClass()).isEqualTo(IllegalAccessException.class);
      }

      assertThat(Methods.createPublicGetterAccessor(EntityB.class, "property3", boolean.class).invoke(entity)).isTrue();
   }

   @Test
   void test_createPublicMethodAccessor() {
      final var entity = new EntityB();

      try {
         Methods.createPublicMethodAccessor(EntityB_getProperty2_Accessor.class, EntityB.class, "getProperty2", int.class).invoke(entity);
         failBecauseExceptionWasNotThrown(ReflectionException.class);
      } catch (final ReflectionException ex) {
         assertThat(asNonNull(ex.getCause()).getClass()).isEqualTo(IllegalAccessException.class);
      }

      entity.setProperty3(true);

      Methods.createPublicMethodAccessor(EntityB_setProperty3_Accessor.class, EntityB.class, "setProperty3", void.class, boolean.class)
         .invoke(entity, false);
      assertThat(Methods.createPublicMethodAccessor(EntityB_isProperty3_Accessor.class, EntityB.class, "isProperty3", boolean.class).invoke(
         entity)).isFalse();

      Methods.createPublicMethodAccessor(EntityB_setProperty3_Accessor.class, EntityB.class, "setProperty3").invoke(entity, true);
      assertThat(Methods.createPublicMethodAccessor(EntityB_isProperty3_Accessor.class, EntityB.class, "isProperty3").invoke(entity))
         .isTrue();
   }

   @Test
   void test_createPublicSetterAccessor() {
      final var entity = new EntityB();
      entity.setProperty3(false);

      try {
         Methods.createPublicSetterAccessor(EntityB.class, "property2", int.class).invoke(entity, 5);
         failBecauseExceptionWasNotThrown(ReflectionException.class);
      } catch (final ReflectionException ex) {
         assertThat(asNonNull(ex.getCause()).getClass()).isEqualTo(IllegalAccessException.class);
      }

      if (SystemUtils.isJavaVersionAtMost(JavaVersion.JAVA_10)) {
         /* TODO on JDK11+ with ECJ compilation results in
          Caused by: java.lang.invoke.LambdaConversionException: Type mismatch for instantiated parameter 1: boolean is not a subtype of class java.lang.Object
             at java.base/java.lang.invoke.AbstractValidatingLambdaMetafactory.checkDescriptor(AbstractValidatingLambdaMetafactory.java:308)
             at java.base/java.lang.invoke.AbstractValidatingLambdaMetafactory.validateMetafactoryArgs(AbstractValidatingLambdaMetafactory.java:294)
             at java.base/java.lang.invoke.LambdaMetafactory.metafactory(LambdaMetafactory.java:328)
             at net.sf.jstuff.core.reflection.Methods.createPublicMethodAccessor(Methods.java:95)
             ... 21 more
           see https://bugs.eclipse.org/bugs/show_bug.cgi?id=546161
           see https://stackoverflow.com/questions/55532055/java-casting-java-11-throws-lambdaconversionexception-while-1-8-does-not
          */
         Methods.createPublicSetterAccessor(EntityB.class, "property3", boolean.class).invoke(entity, true);
         assertThat(entity.isProperty3()).isTrue();
      }
   }

   @Test
   void test_findNonPublicGetterInSuperclass() {
      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2");
         assert m != null;
         assertThat(m.getName()).isEqualTo("getProperty2");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityA.class);
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2", Integer.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("getProperty2");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityA.class);
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2", int.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("getProperty2");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityA.class);
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2", String.class);
         assertThat(m).isNull();
      }
   }

   @Test
   void test_findNonPublicSetterInSuperclass() {
      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2");
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty2");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityA.class);
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2", Integer.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty2");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityA.class);
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2", int.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty2");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityA.class);
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2", String.class);
         assertThat(m).isNull();
      }
   }

   @Test
   void test_findPublicNonOverloadedGetter() {
      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3");
         assert m != null;
         assertThat(m.getName()).isEqualTo("isProperty3");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3", Boolean.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("isProperty3");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3", boolean.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("isProperty3");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3", String.class);
         assertThat(m).isNull();
      }
   }

   @Test
   void test_findPublicNonOverloadedSetter() {
      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3");
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty3");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3", Boolean.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty3");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3", boolean.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty3");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3", String.class);
         assertThat(m).isNull();
      }
   }

   @Test
   void test_findPublicOverloadedGetter() {
      {
         final Method m = Methods.findPublicGetter(EntityB.class, "property1");
         assert m != null;
         assertThat(m.getName()).isEqualTo("getProperty1");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findPublicGetter(EntityB.class, "property1", EntityA.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("getProperty1");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findPublicGetter(EntityB.class, "property1", Integer.class);
         assertThat(m).isNull();
      }
   }

   @Test
   void test_findPublicOverloadedSetter() {
      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1");
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty1");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1", EntityA.class);
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty1");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1", EntityB.class); // assignable check
         assert m != null;
         assertThat(m.getName()).isEqualTo("setProperty1");
         assertThat(m.getDeclaringClass()).isEqualTo(EntityB.class);
      }

      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1", Integer.class);
         assertThat(m).isNull();
      }
   }

   @Test
   void test_getAllGetters() {
      assertThat(Methods.getAllGetters(EntityA.class)).hasSize(2 + 1 /*Object#getClass()*/);
      assertThat(Methods.getAllGetters(EntityB.class)).hasSize(3 + 1 /*Object#getClass()*/);
   }

   @Test
   void test_getAllSetters() {
      assertThat(Methods.getAllSetters(EntityA.class)).hasSize(2);
      assertThat(Methods.getAllSetters(EntityB.class)).hasSize(3);
   }

   @Test
   void test_getPublicGetters() {
      assertThat(Methods.getPublicGetters(EntityA.class)).hasSize(0 + 1 /*Object#getClass()*/);
      assertThat(Methods.getPublicGetters(EntityB.class)).hasSize(2 + 1 /*Object#getClass()*/);
      assertThat(Methods.getPublicGetters(EntityB.class, Boolean.class)).hasSize(1);
      assertThat(Methods.getPublicGetters(EntityB.class, boolean.class)).hasSize(1);
      assertThat(Methods.getPublicGetters(EntityB.class, EntityA.class)).hasSize(1);
      assertThat(Methods.getPublicGetters(EntityB.class, EntityB.class)).hasSize(1);
   }

   @Test
   void test_getPublicSetters() {
      assertThat(Methods.getPublicSetters(EntityA.class)).isEmpty();
      assertThat(Methods.getPublicSetters(EntityB.class)).hasSize(2);
      assertThat(Methods.getPublicSetters(EntityB.class, Boolean.class)).hasSize(1);
      assertThat(Methods.getPublicSetters(EntityB.class, boolean.class)).hasSize(1);
      assertThat(Methods.getPublicSetters(EntityB.class, EntityA.class)).hasSize(1);
      assertThat(Methods.getPublicSetters(EntityB.class, EntityB.class)).hasSize(1);
   }
}
