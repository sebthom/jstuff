/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AuthenticationFailedException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public AuthenticationFailedException() {
   }

   public AuthenticationFailedException(final String message) {
      super(message);
   }

   public AuthenticationFailedException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public AuthenticationFailedException(final Throwable cause) {
      super(cause);
   }
}
