/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
