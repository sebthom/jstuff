/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReceivingAtomBlogsFailedException extends AtomBlogException {
   private static final long serialVersionUID = 1L;

   public ReceivingAtomBlogsFailedException() {
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
