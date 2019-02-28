/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.ref;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LazyInitializedRef<T> implements Ref<T> {
   private T value;

   protected abstract T create();

   public final T get() {
      T result = value;
      if (result == null) {
         synchronized (this) { // ensure only one thread creates an instance
            result = value;
            if (result == null) {
               // the JVM guarantees, that accessing a final reference will return the referenced object fully initialized
               // therefore we are passing new object instance to the final wrapper and accessing indirectly via it's final field
               value = result = new FinalRef<T>(create()).value;
            }
         }
      }
      return result;
   }
}
