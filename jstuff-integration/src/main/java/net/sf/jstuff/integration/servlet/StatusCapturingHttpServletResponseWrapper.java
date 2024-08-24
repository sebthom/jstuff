/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

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
   public void sendError(final int sc, final @Nullable String msg) throws IOException {
      httpStatus = sc;
      super.sendError(sc, msg);
   }

   @Override
   public void setStatus(final int sc) {
      httpStatus = sc;
      super.setStatus(sc);
   }
}
