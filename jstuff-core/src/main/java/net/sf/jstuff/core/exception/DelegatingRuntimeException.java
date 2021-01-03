/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

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
   public synchronized Throwable getCause() {
      return wrapped.getCause();
   }

   @Override
   public String getLocalizedMessage() {
      return wrapped.getLocalizedMessage();
   }

   @Override
   public String getMessage() {
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
   public synchronized DelegatingRuntimeException initCause(final Throwable cause) {
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
   public void setStackTrace(final StackTraceElement[] stackTrace) {
      wrapped.setStackTrace(stackTrace);
   }

   @Override
   public void setWrapped(final Throwable wrapped) {
      throw new UnsupportedOperationException();
   }
}
