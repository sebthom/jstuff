/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DualStreamHandler extends StreamHandler {

   private final StreamHandler stderrHandler;

   private final int maxStdOutLevel;

   public DualStreamHandler(final OutputStream stdout, final OutputStream stderr, final Formatter formatter) {
      this(stdout, stderr, formatter, Level.INFO);
   }

   public DualStreamHandler(final OutputStream stdout, final OutputStream stderr, final Formatter formatter, final Level maxStdOutLevel) {
      super(stdout, formatter);
      Args.notNull("maxLevelStdOut", maxStdOutLevel);

      stderrHandler = new StreamHandler(stderr, formatter);
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

   @Override
   public synchronized void setEncoding(final @Nullable String encoding) throws SecurityException, UnsupportedEncodingException {
      super.setEncoding(encoding);
      stderrHandler.setEncoding(encoding);
   }
}
