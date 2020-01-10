/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
