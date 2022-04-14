/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
