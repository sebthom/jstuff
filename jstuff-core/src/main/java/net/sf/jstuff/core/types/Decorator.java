/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lazyNonNull;

import org.eclipse.jdt.annotation.NonNull;

import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * https://en.wikipedia.org/wiki/Decorator_pattern
 * https://stackoverflow.com/questions/350404/how-do-the-proxy-decorator-adapter-and-bridge-patterns-differ
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Decorator<@NonNull T> {

   abstract class Default<@NonNull T> implements Decorator<T> {
      protected T wrapped = lazyNonNull();
      protected boolean wrappedGettable = true;
      protected boolean wrappedSettable = true;

      protected Default() {
      }

      protected Default(final T wrapped) {
         Args.notNull("wrapped", wrapped);
         this.wrapped = wrapped;
      }

      @Override
      public T getWrapped() {
         Assert.isTrue(isWrappedGettable(), "Accessing the wrapped object is not allowed.");
         return wrapped;
      }

      public void hideWrapped() {
         wrappedGettable = false;
      }

      public void freezeWrapped() {
         wrappedSettable = false;
      }

      @Override
      public boolean isWrappedGettable() {
         return wrappedGettable;
      }

      @Override
      public boolean isWrappedSettable() {
         return wrappedSettable;
      }

      @Override
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
