/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StatusCapturingHttpServletResponseWrapper extends HttpServletResponseWrapper {
   private int httpStatus = SC_OK;

   public StatusCapturingHttpServletResponseWrapper(final HttpServletResponse response) {
      super(response);
   }

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

   @Override
   public void setStatus(final int sc, final String sm) {
      httpStatus = sc;
      super.setStatus(sc, sm);
   }
}
