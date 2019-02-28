/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuntimeInterruptedException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public RuntimeInterruptedException(final InterruptedException cause) {
      super(cause);
   }

   public RuntimeInterruptedException(final String message, final InterruptedException cause) {
      super(message, cause);
   }

   @Override
   public InterruptedException getCause() {
      return (InterruptedException) super.getCause();
   }
}
