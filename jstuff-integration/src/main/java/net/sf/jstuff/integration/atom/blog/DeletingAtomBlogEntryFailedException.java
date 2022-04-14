/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
