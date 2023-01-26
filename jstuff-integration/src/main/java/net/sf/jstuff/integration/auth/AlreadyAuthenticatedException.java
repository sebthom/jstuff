/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AlreadyAuthenticatedException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public AlreadyAuthenticatedException() {
   }

   public AlreadyAuthenticatedException(final String message) {
      super(message);
   }

   public AlreadyAuthenticatedException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public AlreadyAuthenticatedException(final Throwable cause) {
      super(cause);
   }
}
