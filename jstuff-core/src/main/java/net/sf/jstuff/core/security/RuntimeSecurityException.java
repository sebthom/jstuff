/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import java.security.GeneralSecurityException;

import net.sf.jstuff.core.exception.DelegatingRuntimeException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
   public synchronized RuntimeSecurityException fillInStackTrace() {
      super.fillInStackTrace();
      return this;
   }

   @Override
   public GeneralSecurityException getWrapped() {
      return (GeneralSecurityException) super.getWrapped();
   }

   @Override
   public synchronized RuntimeSecurityException initCause(final Throwable cause) {
      super.initCause(cause);
      return this;
   }
}
