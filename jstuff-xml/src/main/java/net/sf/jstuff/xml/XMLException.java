/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

/**
 * Wrapping runtime exception for checked XML related exceptions.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class XMLException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public XMLException(final Throwable cause) {
      super(cause);
   }
}
