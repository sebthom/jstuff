/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JsonRestServiceExporter extends AbstractRestServiceExporter {
   private static final ObjectMapper JSON = new ObjectMapper();

   public JsonRestServiceExporter() {
      super("UTF-8", "application/json;charset=UTF-8");
   }

   @Override
   @SuppressWarnings("resource")
   protected <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException {
      return JSON.readValue(request.getReader(), targetType);
   }

   @Override
   protected String serializeResponse(final Object resultObject) throws JsonProcessingException {
      return JSON.writeValueAsString(resultObject);
   }
}
