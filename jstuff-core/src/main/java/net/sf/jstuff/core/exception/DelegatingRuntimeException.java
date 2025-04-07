/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.types.Decorator;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingRuntimeException extends RuntimeException implements Decorator<Throwable> {

   private static final long serialVersionUID = 1L;

   private final Throwable wrapped;

   public DelegatingRuntimeException(final Throwable wrapped) {
      Args.notNull("wrapped", wrapped);
      this.wrapped = wrapped;
   }

   @SuppressWarnings("null")
   @Override
   public synchronized DelegatingRuntimeException fillInStackTrace() {
      // invoked by parent constructor
      if (wrapped != null) {
         wrapped.fillInStackTrace();
      }
      return this;
   }

   /**
    * @return the cause of the wrapped exception
    */

   @Override
   public synchronized @Nullable Throwable getCause() {
      return wrapped.getCause();
   }

   @Override
   public @Nullable String getLocalizedMessage() {
      return wrapped.getLocalizedMessage();
   }

   @Override
   public @Nullable String getMessage() {
      return wrapped.getMessage();
   }

   /**
    * @return the stacktrace of the wrapped exception
    */
   @Override
   public StackTraceElement[] getStackTrace() {
      return wrapped.getStackTrace();
   }

   /**
    * @return the wrapped exception
    */
   @Override
   public Throwable getWrapped() {
      return wrapped;
   }

   @Override
   public synchronized DelegatingRuntimeException initCause(final @Nullable Throwable cause) {
      wrapped.initCause(cause);
      return this;
   }

   @Override
   public boolean isWrappedGettable() {
      return true;
   }

   @Override
   public boolean isWrappedSettable() {
      return false;
   }

   @Override
   public void printStackTrace() {
      wrapped.printStackTrace();
   }

   @Override
   public void printStackTrace(final PrintStream s) {
      wrapped.printStackTrace(s);
   }

   @Override
   public void printStackTrace(final PrintWriter s) {
      wrapped.printStackTrace(s);
   }

   @Override
   public void setStackTrace(final @NonNull StackTraceElement[] stackTrace) {
      wrapped.setStackTrace(stackTrace);
   }

   @Override
   public void setWrapped(final Throwable wrapped) {
      throw new UnsupportedOperationException();
   }
}
