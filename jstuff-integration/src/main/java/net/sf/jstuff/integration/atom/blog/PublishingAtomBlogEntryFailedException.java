/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.blog;

public class PublishingAtomBlogEntryFailedException extends AtomBlogException {
   private static final long serialVersionUID = 1L;

   public PublishingAtomBlogEntryFailedException() {
      super();
   }

   public PublishingAtomBlogEntryFailedException(final String message) {
      super(message);
   }

   public PublishingAtomBlogEntryFailedException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public PublishingAtomBlogEntryFailedException(final Throwable cause) {
      super(cause);
   }
}
