/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DeletingAtomBlogEntryFailedException extends AtomBlogException {
   private static final long serialVersionUID = 1L;

   public DeletingAtomBlogEntryFailedException() {
   }

   public DeletingAtomBlogEntryFailedException(final String message) {
      super(message);
   }

   public DeletingAtomBlogEntryFailedException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public DeletingAtomBlogEntryFailedException(final Throwable cause) {
      super(cause);
   }
}
