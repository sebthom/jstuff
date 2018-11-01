/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml;

/**
 * Wrapping runtime exception for checked XML related exceptions.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class XMLException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public XMLException(final Throwable cause) {
      super(cause);
   }
}
