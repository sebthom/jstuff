/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Modifiable {

   abstract class Default implements Modifiable {
      protected boolean isModifiable = true;

      public void assertIsModifiable() {
         if (!isModifiable)
            throw new IllegalStateException(this + " is not modifiable!");
      }

      @Override
      public boolean isModifiable() {
         return isModifiable;
      }
   }

   boolean isModifiable();
}
