/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.rest;

import java.io.Serializable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RestServiceError implements Serializable {
   private static final long serialVersionUID = 1L;

   private final String message;
   private final String type;

   public RestServiceError(final String type, final String message) {
      this.type = type;
      this.message = message;
   }

   public String getMessage() {
      return message;
   }

   public String getType() {
      return type;
   }
}
