/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnknownApplicationRoleException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public UnknownApplicationRoleException() {
   }

   public UnknownApplicationRoleException(final String message) {
      super(message);
   }

   public UnknownApplicationRoleException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public UnknownApplicationRoleException(final Throwable cause) {
      super(cause);
   }
}
