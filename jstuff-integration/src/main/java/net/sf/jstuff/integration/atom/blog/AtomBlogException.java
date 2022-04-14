/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlogException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public AtomBlogException() {
   }

   public AtomBlogException(final String message) {
      super(message);
   }

   public AtomBlogException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public AtomBlogException(final Throwable cause) {
      super(cause);
   }
}
