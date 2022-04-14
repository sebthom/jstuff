/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DualPrintStreamHandler extends PrintStreamHandler {

   private final PrintStreamHandler stderrHandler;

   private final int maxStdOutLevel;

   public DualPrintStreamHandler(final PrintStream stdout, final PrintStream stderr, final Formatter formatter) {
      this(stdout, stderr, formatter, Level.INFO);
   }

   public DualPrintStreamHandler(final PrintStream stdout, final PrintStream stderr, final Formatter formatter, final Level maxStdOutLevel) {
      super(stdout, formatter);
      Args.notNull("maxStdOutLevel", maxStdOutLevel);

      stderrHandler = new PrintStreamHandler(stderr, formatter);
      this.maxStdOutLevel = maxStdOutLevel.intValue();
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
   public synchronized void publish(final LogRecord entry) {
      if (isLoggable(entry)) {
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
