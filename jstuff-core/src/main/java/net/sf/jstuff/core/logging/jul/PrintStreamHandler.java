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
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PrintStreamHandler extends Handler {
   private boolean isHeaderPrinted;
   private final PrintStream out;

   public PrintStreamHandler(final PrintStream out, final Formatter formatter) {
      setLevel(Level.INFO);
      setFormatter(formatter);
      this.out = out;
   }

   @Override
   public synchronized void close() throws SecurityException {
      try {
         if (!isHeaderPrinted) {
            out.print(getFormatter().getHead(this));
            isHeaderPrinted = true;
         }
         out.print(getFormatter().getTail(this));
         flush();
         out.close();
      } catch (final Exception ex) {
         reportError(null, ex, ErrorManager.CLOSE_FAILURE);
      }
   }

   @Override
   public synchronized void flush() {
      try {
         out.flush();
      } catch (final Exception ex) {
         reportError(null, ex, ErrorManager.FLUSH_FAILURE);
      }
   }

   /**
    * Not supported.
    */
   @Override
   public String getEncoding() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isLoggable(final LogRecord record) {
      return record != null && super.isLoggable(record);
   }

   @Override
   public synchronized void publish(final LogRecord record) {
      if (isLoggable(record)) {
         try {
            final String msg = getFormatter().format(record);
            try {
               if (!isHeaderPrinted) {
                  out.print(getFormatter().getHead(this));
                  isHeaderPrinted = true;
               }
               out.print(msg);
            } catch (final Exception ex) {
               reportError(null, ex, ErrorManager.WRITE_FAILURE);
            }
         } catch (final Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
         }
      }
   }

   /**
    * Not supported.
    */
   @Override
   public void setEncoding(final String encoding) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
