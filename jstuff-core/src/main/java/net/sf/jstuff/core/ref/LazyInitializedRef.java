/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LazyInitializedRef<T> implements Ref<T> {
   private T value;

   protected abstract T create();

   @Override
   public final T get() {
      T result = value;
      if (result == null) {
         synchronized (this) { // ensure only one thread creates an instance
            result = value;
            if (result == null) {
               // the JVM guarantees, that accessing a final reference will return the referenced object fully initialized
               // therefore we are passing new object instance to the final wrapper and accessing indirectly via it's final field
               result = new FinalRef<>(create()).value;
               value = result;
            }
         }
      }
      return result;
   }
}
