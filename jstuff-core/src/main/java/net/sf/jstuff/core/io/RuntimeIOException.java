/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.exception.DelegatingRuntimeException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuntimeIOException extends DelegatingRuntimeException {
   private static final long serialVersionUID = 1L;

   public RuntimeIOException(final IOException cause) {
      super(cause);
   }

   @Override
   public synchronized RuntimeIOException fillInStackTrace() {
      super.fillInStackTrace();
      return this;
   }

   @Override
   public IOException getWrapped() {
      return (IOException) super.getWrapped();
   }

   @Override
   public synchronized RuntimeIOException initCause(final @Nullable Throwable cause) {
      super.initCause(cause);
      return this;
   }
}
