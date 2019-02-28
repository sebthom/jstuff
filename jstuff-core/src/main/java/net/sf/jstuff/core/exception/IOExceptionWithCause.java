/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.exception;

import java.io.IOException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IOExceptionWithCause extends IOException {

   private static final long serialVersionUID = 1L;

   public IOExceptionWithCause() {
      super();
   }

   public IOExceptionWithCause(final String msg) {
      super(msg);
   }

   public IOExceptionWithCause(final String msg, final Throwable cause) {
      this(msg);
      initCause(cause);
   }

   public IOExceptionWithCause(final Throwable cause) {
      this();
      initCause(cause);
   }
}
