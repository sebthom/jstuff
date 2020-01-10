/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.exception;

/**
 * Lightweight unchecked exception without stack trace information that can be used for flow control.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastRuntimeException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public FastRuntimeException() {
   }

   public FastRuntimeException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public FastRuntimeException(final String message) {
      super(message);
   }

   public FastRuntimeException(final Throwable cause) {
      super(cause);
   }

   @Override
   public Throwable fillInStackTrace() {
      return null;
   }
}
