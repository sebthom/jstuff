/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
