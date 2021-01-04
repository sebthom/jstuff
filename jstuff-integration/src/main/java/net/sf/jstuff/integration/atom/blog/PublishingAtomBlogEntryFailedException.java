/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PublishingAtomBlogEntryFailedException extends AtomBlogException {
   private static final long serialVersionUID = 1L;

   public PublishingAtomBlogEntryFailedException() {
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
