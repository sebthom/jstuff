/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.builder;

import junit.framework.TestCase;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BuilderTest extends TestCase {

   public static class EntityA {

      public interface EntityABuilder<THIS extends EntityABuilder<THIS, T>, T extends EntityA> extends Builder<T> {

         @Builder.Property(required = true, nullable = false)
         THIS propertyA(String value);

         THIS propertyB(int value);

         THIS withMethodCall(String name, int value);
      }

      @SuppressWarnings("unchecked")
      public static EntityABuilder<?, ? extends EntityA> builder() {
         return (EntityABuilder<?, ? extends EntityA>) BuilderFactory.of(EntityABuilder.class).create();
      }

      protected String propertyA;
      protected Integer propertyB = -1;
      protected int methodCallCount = 0;

      @OnPostBuild
      protected void onInitialized() {
         Args.notNull("propertyB", propertyB);
         Args.notNegative("propertyB", propertyB);
      }

      @SuppressWarnings("unused")
      protected void setMethodCall(final String name, final int value) {
         methodCallCount++;
      }
   }

   public static class EntityB extends EntityA {

      public interface EntityBBuilder<THIS extends EntityBBuilder<THIS, T>, T extends EntityB> extends EntityABuilder<THIS, T> {

         THIS propertyC(Long value);

         THIS propertyD(String value);

         @Builder.Property(required = true, nullable = true)
         THIS withPropertyE(String value);

      }

      @SuppressWarnings("unchecked")
      public static EntityBBuilder<?, ? extends EntityB> builder() {
         return (EntityBBuilder<?, ? extends EntityB>) BuilderFactory.of(EntityBBuilder.class).create();
      }

      public long propertyC = -1;
      private String propertyD;
      protected String propertyE;

      @Override
      protected void onInitialized() {
         super.onInitialized();
         Args.notNegative("propertyC", propertyC);
         if (!propertyD.endsWith("_setWithSetter"))
            throw new IllegalArgumentException("propertyD not set via setter");
      }

      public void setPropertyD(final String value) {
         propertyD = value + "_setWithSetter";
      }

   }

   public void testEntityABuilder() {
      EntityA.builder() //
         .propertyA("foo") //
         .propertyB(1) //
         .build();

      try {
         EntityA.builder() //
            .build();
         fail();
      } catch (final Exception ex) {
         assertEquals("Setting EntityABuilder.propertyA(...) is required.", ex.getMessage());
      }

      try {
         EntityA.builder() //
            .propertyA(null) //
            .build();
         fail();
      } catch (final Exception ex) {
         assertEquals(ex.getMessage(), "EntityABuilder.propertyA(...) must not be set to null.");
      }

      try {
         EntityA.builder() //
            .propertyA("foo") //
            .build();
         fail();
      } catch (final Exception ex) { /* expected */ }

      try {
         EntityA.builder() //
            .propertyA("foo") //
            .propertyB(-1) //
            .build();
         fail();
      } catch (final Exception ex) { /* expected */ }
   }

   public void testEntityBBuilder() {
      final EntityB entity = EntityB.builder() //
         .propertyA("foo") //
         .propertyB(1) //
         .propertyC(3L) //
         .propertyD("bar") //
         .withPropertyE("ee") //
         .withMethodCall("cat", 22) //
         .withMethodCall("dog", 33) //
         .build();

      assertEquals(2, entity.methodCallCount);

      try {
         EntityB.builder() //
            .propertyB(1) //
            .propertyC(3L) //
            .propertyD("bar") //
            .withPropertyE("ee") //
            .build();
         fail();
      } catch (final Exception ex) {
         assertEquals("Setting EntityBBuilder.propertyA(...) is required.", ex.getMessage());
      }

      try {
         EntityB.builder() //
            .propertyA(null) //
            .propertyB(1) //
            .propertyC(3L) //
            .propertyD("bar") //
            .withPropertyE("ee") //
            .build();
         fail();
      } catch (final Exception ex) {
         assertEquals(ex.getMessage(), "EntityBBuilder.propertyA(...) must not be set to null.");
      }

      try {
         EntityB.builder() //
            .propertyC(3L) //
            .propertyD("bar") //
            .build();
         fail();
      } catch (final Exception ex) { /* expected */ }
      try {
         EntityB.builder() //
            .propertyA("foo") //
            .propertyB(1) //
            .propertyC(-1L) //
            .propertyD("bar") //
            .build();
         fail();
      } catch (final Exception ex) { /* expected */ }

      try {
         EntityB.builder() //
            .propertyA("") //
            .propertyB(1) //
            .propertyC(1L) //
            .propertyD("foo") //
            .build();
         fail();
      } catch (final Exception ex) {
         assertEquals("Setting EntityBBuilder.withPropertyE(...) is required.", ex.getMessage());
      }

      EntityB.builder() //
         .propertyA("") //
         .propertyB(1) //
         .propertyC(1L) //
         .propertyD("foo") //
         .withPropertyE(null) //
         .build();
   }
}
