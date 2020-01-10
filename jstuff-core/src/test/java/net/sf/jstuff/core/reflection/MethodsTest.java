/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.jstuff.core.reflection.MethodsTestEntities.EntityA;
import net.sf.jstuff.core.reflection.MethodsTestEntities.EntityB;
import net.sf.jstuff.core.reflection.exception.ReflectionException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MethodsTest extends TestCase {

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

   public void test_createPublicGetterAccessor() {
      final EntityB entity = new EntityB();
      entity.setProperty3(true);

      try {
         Methods.createPublicGetterAccessor(EntityB.class, "property2", int.class).invoke(entity);
      } catch (final ReflectionException ex) {
         assertEquals(IllegalAccessException.class, ex.getCause().getClass());
      }

      assertEquals(true, (boolean) Methods.createPublicGetterAccessor(EntityB.class, "property3", boolean.class).invoke(entity));
   }

   public void test_createPublicMethodAccessor() {
      final EntityB entity = new EntityB();

      try {
         Methods.createPublicMethodAccessor(EntityB_getProperty2_Accessor.class, EntityB.class, "getProperty2", int.class).invoke(entity);
      } catch (final ReflectionException ex) {
         assertEquals(IllegalAccessException.class, ex.getCause().getClass());
      }

      entity.setProperty3(true);

      Methods.createPublicMethodAccessor(EntityB_setProperty3_Accessor.class, EntityB.class, "setProperty3", void.class, boolean.class).invoke(entity, false);
      assertEquals(false, Methods.createPublicMethodAccessor(EntityB_isProperty3_Accessor.class, EntityB.class, "isProperty3", boolean.class).invoke(entity));

      Methods.createPublicMethodAccessor(EntityB_setProperty3_Accessor.class, EntityB.class, "setProperty3").invoke(entity, true);
      assertEquals(true, Methods.createPublicMethodAccessor(EntityB_isProperty3_Accessor.class, EntityB.class, "isProperty3").invoke(entity));
   }

   public void test_createPublicSetterAccessor() {
      final EntityB entity = new EntityB();
      entity.setProperty3(false);

      try {
         Methods.createPublicSetterAccessor(EntityB.class, "property2", int.class).invoke(entity, 5);
      } catch (final ReflectionException ex) {
         assertEquals(IllegalAccessException.class, ex.getCause().getClass());
      }

      Methods.createPublicSetterAccessor(EntityB.class, "property3", boolean.class).invoke(entity, true);
      assertEquals(true, entity.isProperty3());
   }

   public void test_findNonPublicGetterInSuperclass() {
      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2");
         assertNotNull(m);
         assertEquals("getProperty2", m.getName());
         assertEquals(EntityA.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2", Integer.class);
         assertNotNull(m);
         assertEquals("getProperty2", m.getName());
         assertEquals(EntityA.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2", int.class);
         assertNotNull(m);
         assertEquals("getProperty2", m.getName());
         assertEquals(EntityA.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property2", String.class);
         assertNull(m);
      }
   }

   public void test_findNonPublicSetterInSuperclass() {
      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2");
         assertNotNull(m);
         assertEquals("setProperty2", m.getName());
         assertEquals(EntityA.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2", Integer.class);
         assertNotNull(m);
         assertEquals("setProperty2", m.getName());
         assertEquals(EntityA.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2", int.class);
         assertNotNull(m);
         assertEquals("setProperty2", m.getName());
         assertEquals(EntityA.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property2", String.class);
         assertNull(m);
      }
   }

   public void test_findPublicNonOverloadedGetter() {

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3");
         assertNotNull(m);
         assertEquals("isProperty3", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3", Boolean.class);
         assertNotNull(m);
         assertEquals("isProperty3", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3", boolean.class);
         assertNotNull(m);
         assertEquals("isProperty3", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnyGetter(EntityB.class, "property3", String.class);
         assertNull(m);
      }
   }

   public void test_findPublicNonOverloadedSetter() {

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3");
         assertNotNull(m);
         assertEquals("setProperty3", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3", Boolean.class);
         assertNotNull(m);
         assertEquals("setProperty3", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3", boolean.class);
         assertNotNull(m);
         assertEquals("setProperty3", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findAnySetter(EntityB.class, "property3", String.class);
         assertNull(m);
      }
   }

   public void test_findPublicOverloadedGetter() {
      {
         final Method m = Methods.findPublicGetter(EntityB.class, "property1");
         assertNotNull(m);
         assertEquals("getProperty1", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findPublicGetter(EntityB.class, "property1", EntityA.class);
         assertNotNull(m);
         assertEquals("getProperty1", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findPublicGetter(EntityB.class, "property1", Integer.class);
         assertNull(m);
      }
   }

   public void test_findPublicOverloadedSetter() {
      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1");
         assertNotNull(m);
         assertEquals("setProperty1", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1", EntityA.class);
         assertNotNull(m);
         assertEquals("setProperty1", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1", EntityB.class); // assignable check
         assertNotNull(m);
         assertEquals("setProperty1", m.getName());
         assertEquals(EntityB.class, m.getDeclaringClass());
      }

      {
         final Method m = Methods.findPublicSetter(EntityB.class, "property1", Integer.class);
         assertNull(m);
      }
   }

   public void test_getAllGetters() {
      assertEquals(2 + 1 /*Object#getClass()*/, Methods.getAllGetters(EntityA.class).size());
      assertEquals(3 + 1 /*Object#getClass()*/, Methods.getAllGetters(EntityB.class).size());
   }

   public void test_getAllSetters() {
      assertEquals(2, Methods.getAllSetters(EntityA.class).size());
      assertEquals(3, Methods.getAllSetters(EntityB.class).size());
   }

   public void test_getPublicGetters() {
      assertEquals(0 + 1 /*Object#getClass()*/, Methods.getPublicGetters(EntityA.class).size());
      assertEquals(2 + 1 /*Object#getClass()*/, Methods.getPublicGetters(EntityB.class).size());
      assertEquals(1, Methods.getPublicGetters(EntityB.class, Boolean.class).size());
      assertEquals(1, Methods.getPublicGetters(EntityB.class, boolean.class).size());
      assertEquals(1, Methods.getPublicGetters(EntityB.class, EntityA.class).size());
      assertEquals(1, Methods.getPublicGetters(EntityB.class, EntityB.class).size());
   }

   public void test_getPublicSetters() {
      assertEquals(0, Methods.getPublicSetters(EntityA.class).size());
      assertEquals(2, Methods.getPublicSetters(EntityB.class).size());
      assertEquals(1, Methods.getPublicSetters(EntityB.class, Boolean.class).size());
      assertEquals(1, Methods.getPublicSetters(EntityB.class, boolean.class).size());
      assertEquals(1, Methods.getPublicSetters(EntityB.class, EntityA.class).size());
      assertEquals(1, Methods.getPublicSetters(EntityB.class, EntityB.class).size());
   }
}
