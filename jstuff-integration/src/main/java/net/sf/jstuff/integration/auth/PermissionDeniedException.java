/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PermissionDeniedException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public PermissionDeniedException() {
   }

   public PermissionDeniedException(final String message) {
      super(message);
   }

   public PermissionDeniedException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public PermissionDeniedException(final Throwable cause) {
      super(cause);
   }
}
