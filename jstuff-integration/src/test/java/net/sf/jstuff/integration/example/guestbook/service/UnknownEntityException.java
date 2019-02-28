/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.example.guestbook.service;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnknownEntityException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public UnknownEntityException(final Class<?> type, final Integer id) {
      super("Unknown entity of type [" + type.getSimpleName() + "] with id [" + id + "]");
   }
}
