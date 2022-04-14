/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.exception;

/**
 * Lightweight checked exception without stack trace information that can be used for flow control.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastException extends Exception {
   private static final long serialVersionUID = 1L;

   public FastException() {
   }

   public FastException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public FastException(final String message) {
      super(message);
   }

   public FastException(final Throwable cause) {
      super(cause);
   }

   @Override
   public Throwable fillInStackTrace() {
      return null;
   }
}
