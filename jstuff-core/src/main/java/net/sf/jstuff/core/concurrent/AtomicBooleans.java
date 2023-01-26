/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AtomicBooleans {

   /**
    * https://stackoverflow.com/a/45700319/5116073
    */
   public static boolean negate(final AtomicBoolean ab) {
      Args.notNull("ab", ab);

      boolean newVal = !ab.get();

      // try to set the new value if the current value is the opposite of the new value
      while (!ab.compareAndSet(!newVal, newVal)) {
         // if the value we try to set was already set in the mean-time (by other code
         // in another thread) then toggle the new value and try again
         newVal = !newVal;
      }
      // return the value we finally could set
      return newVal;
   }
}
