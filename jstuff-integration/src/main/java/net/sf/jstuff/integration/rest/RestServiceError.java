/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
