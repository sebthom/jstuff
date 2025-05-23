/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Test classes are placed in a separate place because of
 * https://stackoverflow.com/questions/34763339/methodhandle-private-method-called-using-findvirtual
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
abstract class MethodsTestEntities {

   static class EntityA {
      private @Nullable EntityA property1;
      private int property2;

      protected @Nullable EntityA getProperty1() {
         return property1;
      }

      @SuppressWarnings("unused")
      private int getProperty2() {
         return property2;
      }

      protected void setProperty1(final EntityA property1) {
         this.property1 = property1;
      }

      @SuppressWarnings("unused")
      private void setProperty2(final int property2) {
         this.property2 = property2;
      }

   }

   static class EntityB extends EntityA {

      private boolean property3;

      @Override
      public @Nullable EntityA getProperty1() {
         return super.getProperty1();
      }

      public boolean isProperty3() {
         return property3;
      }

      @Override
      public void setProperty1(final EntityA property1) {
         super.setProperty1(property1);
      }

      public void setProperty3(final boolean property3) {
         this.property3 = property3;
      }
   }
}
