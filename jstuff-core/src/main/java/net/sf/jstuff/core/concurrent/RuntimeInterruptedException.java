/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.exception.DelegatingRuntimeException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuntimeInterruptedException extends DelegatingRuntimeException {
   private static final long serialVersionUID = 1L;

   public RuntimeInterruptedException(final InterruptedException cause) {
      super(cause);
   }

   @Override
   public synchronized RuntimeInterruptedException fillInStackTrace() {
      super.fillInStackTrace();
      return this;
   }

   @Override
   public RuntimeInterruptedException getWrapped() {
      return (RuntimeInterruptedException) super.getWrapped();
   }

   @Override
   public synchronized RuntimeInterruptedException initCause(final @Nullable Throwable cause) {
      super.initCause(cause);
      return this;
   }
}
