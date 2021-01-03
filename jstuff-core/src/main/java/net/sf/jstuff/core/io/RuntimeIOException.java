/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.io.IOException;

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
   public synchronized RuntimeIOException initCause(final Throwable cause) {
      super.initCause(cause);
      return this;
   }
}
