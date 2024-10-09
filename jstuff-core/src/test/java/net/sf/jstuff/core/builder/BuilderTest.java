/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.builder;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class BuilderTest {

   static class EntityA {

      interface EntityABuilder<THIS extends EntityABuilder<THIS, T>, T extends EntityA> extends Builder<T> {

         @Builder.Property(required = true, nullable = false)
         THIS propertyA(@Nullable String value);

         THIS propertyB(int value);

         THIS withMethodCall(String name, int value);
      }

      @SuppressWarnings("unchecked")
      static EntityABuilder<?, ? extends EntityA> builder() {
         return (EntityABuilder<?, ? extends EntityA>) BuilderFactory.of(EntityABuilder.class).create();
      }

      @Nullable
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

   static class EntityB extends EntityA {

      interface EntityBBuilder<THIS extends EntityBBuilder<THIS, T>, T extends EntityB> extends EntityABuilder<THIS, T> {

         THIS propertyC(Long value);

         THIS propertyD(String value);

         @Builder.Property(required = true, nullable = true)
         THIS withPropertyE(@Nullable String value);

      }

      @SuppressWarnings("unchecked")
      static EntityBBuilder<?, ? extends EntityB> builder() {
         return (EntityBBuilder<?, ? extends EntityB>) BuilderFactory.of(EntityBBuilder.class).create();
      }

      long propertyC = -1;
      @Nullable
      private String propertyD;
      @Nullable
      protected String propertyE;

      @Override
      protected void onInitialized() {
         super.onInitialized();
         Args.notNegative("propertyC", propertyC);
         final var propertyD = this.propertyD;
         if (propertyD == null || !propertyD.endsWith("_setWithSetter"))
            throw new IllegalArgumentException("propertyD not set via setter");
      }

      void setPropertyD(final String value) {
         propertyD = value + "_setWithSetter";
      }
   }

   @Test
   void testEntityABuilder() {
      EntityA.builder() //
         .propertyA("foo") //
         .propertyB(1) //
         .build();

      try {
         EntityA.builder() //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) {
         assertThat(ex.getMessage()).isEqualTo("Setting EntityABuilder.propertyA(...) is required.");
      }

      try {
         EntityA.builder() //
            .propertyA(null) //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) {
         assertThat(ex.getMessage()).isEqualTo("EntityABuilder.propertyA(...) must not be set to null.");
      }

      try {
         EntityA.builder() //
            .propertyA("foo") //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) { /* expected */ }

      try {
         EntityA.builder() //
            .propertyA("foo") //
            .propertyB(-1) //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) { /* expected */ }
   }

   @Test
   void testEntityBBuilder() {
      final EntityB entity = EntityB.builder() //
         .propertyA("foo") //
         .propertyB(1) //
         .propertyC(3L) //
         .propertyD("bar") //
         .withPropertyE("ee") //
         .withMethodCall("cat", 22) //
         .withMethodCall("dog", 33) //
         .build();

      assertThat(entity.methodCallCount).isEqualTo(2);

      try {
         EntityB.builder() //
            .propertyB(1) //
            .propertyC(3L) //
            .propertyD("bar") //
            .withPropertyE("ee") //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) {
         assertThat(ex.getMessage()).isEqualTo("Setting EntityBBuilder.propertyA(...) is required.");
      }

      try {
         EntityB.builder() //
            .propertyA(null) //
            .propertyB(1) //
            .propertyC(3L) //
            .propertyD("bar") //
            .withPropertyE("ee") //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) {
         assertThat(ex.getMessage()).isEqualTo("EntityBBuilder.propertyA(...) must not be set to null.");
      }

      try {
         EntityB.builder() //
            .propertyC(3L) //
            .propertyD("bar") //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) { /* expected */ }
      try {
         EntityB.builder() //
            .propertyA("foo") //
            .propertyB(1) //
            .propertyC(-1L) //
            .propertyD("bar") //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) { /* expected */ }

      try {
         EntityB.builder() //
            .propertyA("") //
            .propertyB(1) //
            .propertyC(1L) //
            .propertyD("foo") //
            .build();
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) {
         assertThat(ex.getMessage()).isEqualTo("Setting EntityBBuilder.withPropertyE(...) is required.");
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
