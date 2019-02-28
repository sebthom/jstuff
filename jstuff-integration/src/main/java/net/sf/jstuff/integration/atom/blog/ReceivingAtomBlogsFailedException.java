/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.blog;

public class ReceivingAtomBlogsFailedException extends AtomBlogException {
   private static final long serialVersionUID = 1L;

   public ReceivingAtomBlogsFailedException() {
      super();
   }

   public ReceivingAtomBlogsFailedException(final String message) {
      super(message);
   }

   public ReceivingAtomBlogsFailedException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public ReceivingAtomBlogsFailedException(final Throwable cause) {
      super(cause);
   }

}
