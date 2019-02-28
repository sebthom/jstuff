/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * https://en.wikipedia.org/wiki/Decorator_pattern
 * https://stackoverflow.com/questions/350404/how-do-the-proxy-decorator-adapter-and-bridge-patterns-differ
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Decorator<T> {

   abstract class Default<T> implements Decorator<T> {
      protected T wrapped;

      protected Default() {
      }

      protected Default(final T wrapped) {
         Args.notNull("wrapped", wrapped);
         this.wrapped = wrapped;
      }

      public T getWrapped() {
         Assert.isTrue(isWrappedGettable(), "Accessing the wrapped object is not allowed.");
         return wrapped;
      }

      public boolean isWrappedGettable() {
         return true;
      }

      public boolean isWrappedSettable() {
         return true;
      }

      public void setWrapped(final T wrapped) {
         Assert.isTrue(isWrappedSettable(), "Changing the wrapped object is not allowed.");
         Args.notNull("wrapped", wrapped);
         Assert.isFalse(wrapped == this, "[wrapped] must not be a self-reference.");
         this.wrapped = wrapped;
      }
   }

   /**
    * @throws IllegalStateException if getting the wrapped object is disallowed
    */
   T getWrapped();

   boolean isWrappedGettable();

   boolean isWrappedSettable();

   /**
    * @throws IllegalStateException if setting the wrapped object is disallowed
    */
   void setWrapped(T wrapped);
}
