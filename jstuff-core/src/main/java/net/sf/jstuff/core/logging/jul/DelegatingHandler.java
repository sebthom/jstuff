/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.io.UnsupportedEncodingException;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingHandler extends Handler {
   protected final Handler wrapped;

   public DelegatingHandler(final Handler handler) {
      Args.notNull("handler", handler);
      wrapped = handler;
   }

   @Override
   public void close() throws SecurityException {
      wrapped.close();
   }

   @Override
   public void flush() {
      wrapped.flush();
   }

   @Override
   public String getEncoding() {
      return wrapped.getEncoding();
   }

   @Override
   public ErrorManager getErrorManager() {
      return wrapped.getErrorManager();
   }

   @Override
   public Filter getFilter() {
      return wrapped.getFilter();
   }

   @Override
   public Formatter getFormatter() {
      return wrapped.getFormatter();
   }

   @Override
   public Level getLevel() {
      return wrapped.getLevel();
   }

   @Override
   public boolean isLoggable(final LogRecord record) {
      return wrapped.isLoggable(record);
   }

   @Override
   public void publish(final LogRecord record) {
      wrapped.publish(record);
   }

   @Override
   public synchronized void setEncoding(final String encoding) throws SecurityException, UnsupportedEncodingException {
      wrapped.setEncoding(encoding);
   }

   @Override
   public synchronized void setErrorManager(final ErrorManager em) {
      wrapped.setErrorManager(em);
   }

   @Override
   public synchronized void setFilter(final Filter newFilter) throws SecurityException {
      wrapped.setFilter(newFilter);
   }

   @Override
   public synchronized void setFormatter(final Formatter newFormatter) throws SecurityException {
      wrapped.setFormatter(newFormatter);
   }

   @Override
   public synchronized void setLevel(final Level newLevel) throws SecurityException {
      wrapped.setLevel(newLevel);
   }

}
