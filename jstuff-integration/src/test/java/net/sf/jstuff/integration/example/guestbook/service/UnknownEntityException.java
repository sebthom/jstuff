/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.example.guestbook.service;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnknownEntityException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public UnknownEntityException(final Class<?> type, final Integer id) {
      super("Unknown entity of type [" + type.getSimpleName() + "] with id [" + id + "]");
   }
}
