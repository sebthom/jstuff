/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.logging.jul;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DualPrintStreamHandler extends PrintStreamHandler {

   private final PrintStreamHandler stderrHandler;

   public DualPrintStreamHandler(final PrintStream stdout, final PrintStream stderr, final Formatter formatter) {
      super(stdout, formatter);
      stderrHandler = new PrintStreamHandler(stderr, formatter);
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
   public synchronized void setEncoding(final String encoding) {
      super.setEncoding(encoding);
      stderrHandler.setEncoding(encoding);
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
}
