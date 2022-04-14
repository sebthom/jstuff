/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StatusCapturingHttpServletResponseWrapper extends HttpServletResponseWrapper {
   private int httpStatus = SC_OK;

   public StatusCapturingHttpServletResponseWrapper(final HttpServletResponse response) {
      super(response);
   }

   @Override
   public int getStatus() {
      return httpStatus;
   }

   @Override
   public void sendError(final int sc) throws IOException {
      httpStatus = sc;
      super.sendError(sc);
   }

   @Override
   public void sendError(final int sc, final String msg) throws IOException {
      httpStatus = sc;
      super.sendError(sc, msg);
   }

   @Override
   public void setStatus(final int sc) {
      httpStatus = sc;
      super.setStatus(sc);
   }

   /**
    * @deprecated As of version 2.1, due to ambiguous meaning of the
    *             message parameter. To set a status code
    *             use {@link #setStatus(int)}, to send an error with a description
    *             use {@link #sendError(int, String)}
    */
   @Deprecated
   @Override
   public void setStatus(final int sc, final String sm) {
      httpStatus = sc;
      super.setStatus(sc, sm);
   }
}
