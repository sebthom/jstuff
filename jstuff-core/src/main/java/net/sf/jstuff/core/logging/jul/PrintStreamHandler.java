/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.io.PrintStream;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PrintStreamHandler extends Handler {
   private boolean isHeaderPrinted;
   private final PrintStream out;
   private Formatter formatter;

   public PrintStreamHandler(final PrintStream out, final Formatter formatter) {
      setLevel(Level.INFO);
      this.formatter = formatter;
      setFormatter(formatter);
      this.out = out;
   }

   @Override
   public synchronized void close() throws SecurityException {
      try {
         if (!isHeaderPrinted) {
            out.print(formatter.getHead(this));
            isHeaderPrinted = true;
         }
         out.print(formatter.getTail(this));
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
   @Nullable
   @Override
   public String getEncoding() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public synchronized void publish(final @Nullable LogRecord entry) {
      if (entry != null && isLoggable(entry)) {
         try {
            final String msg = formatter.format(entry);
            try {
               if (!isHeaderPrinted) {
                  out.print(formatter.getHead(this));
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
   public void setEncoding(final @Nullable String encoding) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public synchronized void setFormatter(final Formatter newFormatter) throws SecurityException {
      super.setFormatter(newFormatter);
      formatter = newFormatter;
   }
}
