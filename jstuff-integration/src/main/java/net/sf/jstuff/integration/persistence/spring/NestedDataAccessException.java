/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.persistence.spring;

import org.springframework.dao.DataAccessException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NestedDataAccessException extends DataAccessException {
   private static final long serialVersionUID = 1L;

   public NestedDataAccessException(final String msg, final Throwable cause) {
      super(msg, cause);
   }
}
