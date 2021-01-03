/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.logging.jul;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DualStreamHandler extends StreamHandler {

   private final StreamHandler stderrHandler;

   public DualStreamHandler(final OutputStream stdout, final OutputStream stderr, final Formatter formatter) {
      super(stdout, formatter);
      stderrHandler = new StreamHandler(stderr, formatter);
   }

   @Override
   public synchronized void close() throws SecurityException {
      try {
         super.close();
      } finally {
         stderrHandler.close();
      }
   }

   @Override
   public synchronized void flush() {
      super.flush();
      stderrHandler.flush();
   }

   @Override
   public synchronized void publish(final LogRecord record) {
      if (isLoggable(record)) {
         if (record.getLevel().intValue() > Levels.INFO_INT) {
            super.flush();
            stderrHandler.publish(record);
            stderrHandler.flush();
         } else {
            super.publish(record);
         }
      }
   }

   @Override
   public synchronized void setEncoding(final String encoding) throws SecurityException, UnsupportedEncodingException {
      super.setEncoding(encoding);
      stderrHandler.setEncoding(encoding);
   }
}
