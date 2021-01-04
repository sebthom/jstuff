/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.rest;

import java.io.Serializable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
