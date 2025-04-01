/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DualPrintStreamHandler extends PrintStreamHandler {

   private final PrintStreamHandler stderrHandler;

   private final int maxStdOutLevel;

   public DualPrintStreamHandler(final PrintStream stdout, final PrintStream stderr, final Formatter formatter) {
      this(stdout, stderr, formatter, Level.INFO);
   }

   public DualPrintStreamHandler(final PrintStream stdout, final PrintStream stderr, final Formatter formatter,
         final Level maxStdOutLevel) {
      super(stdout, formatter);

      this.maxStdOutLevel = maxStdOutLevel.intValue();
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
   public synchronized void setEncoding(final @Nullable String encoding) {
      super.setEncoding(encoding);
      stderrHandler.setEncoding(encoding);
   }

   @Override
   public synchronized void publish(final @Nullable LogRecord entry) {
      if (entry != null && isLoggable(entry)) {
         if (entry.getLevel().intValue() > maxStdOutLevel) {
            super.flush();
            stderrHandler.publish(entry);
            stderrHandler.flush();
         } else {
            super.publish(entry);
         }
      }
   }
}
