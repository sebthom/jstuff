/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import java.security.GeneralSecurityException;

import net.sf.jstuff.core.exception.DelegatingRuntimeException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuntimeSecurityException extends DelegatingRuntimeException {
   private static final long serialVersionUID = 1L;

   public RuntimeSecurityException(final String message) {
      super(new GeneralSecurityException(message));
   }

   public RuntimeSecurityException(final GeneralSecurityException cause) {
      super(cause);
   }

   @Override
   public RuntimeSecurityException fillInStackTrace() {
      super.fillInStackTrace();
      return this;
   }

   @Override
   public GeneralSecurityException getWrapped() {
      return (GeneralSecurityException) super.getWrapped();
   }

   @Override
   public RuntimeSecurityException initCause(final Throwable cause) {
      super.initCause(cause);
      return this;
   }
}
