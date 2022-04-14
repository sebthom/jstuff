/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.exception;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReflectionException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public ReflectionException(final String message) {
      super(message);
   }

   public ReflectionException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public ReflectionException(final Throwable cause) {
      super(cause);
   }
}
