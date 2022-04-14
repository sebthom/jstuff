/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.spring;

import org.springframework.dao.DataAccessException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NestedDataAccessException extends DataAccessException {
   private static final long serialVersionUID = 1L;

   public NestedDataAccessException(final String msg, final Throwable cause) {
      super(msg, cause);
   }
}
