/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.logging;

import java.lang.reflect.Method;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
final class DelegatingLogger extends Decorator.Default<LoggerInternal> implements LoggerInternal {

   static final String FQCN = DelegatingLogger.class.getName();

   DelegatingLogger(final LoggerInternal delegate) {
      super(delegate);
   }

   @Override
   public void debug(final String msg) {
      wrapped.debug(msg);
   }

   @Override
   public void debug(final String messageTemplate, final Object arg) {
      wrapped.debug(messageTemplate, arg);
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2) {
      wrapped.debug(messageTemplate, arg1, arg2);
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      wrapped.debug(messageTemplate, arg1, arg2, arg3);
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      wrapped.debug(messageTemplate, arg1, arg2, arg3, arg4);
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      wrapped.debug(messageTemplate, arg1, arg2, arg3, arg4, arg5);
   }

   @Override
   public void debug(final Throwable ex) {
      wrapped.debug(ex);
   }

   @Override
   public void debug(final Throwable ex, final String msg) {
      wrapped.debug(ex, msg);
   }

   @Override
   public void debug(final Throwable ex, final String messageTemplate, final Object... args) {
      wrapped.debug(ex, messageTemplate, args);
   }

   @Override
   public void debugNew(final Object newInstance) {
      wrapped.debugNew(newInstance);
   }

   @Override
   public void error(final String msg) {
      wrapped.error(msg);
   }

   @Override
   public void error(final String messageTemplate, final Object arg) {
      wrapped.error(messageTemplate, arg);
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2) {
      wrapped.error(messageTemplate, arg1, arg2);
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      wrapped.error(messageTemplate, arg1, arg2, arg3);
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      wrapped.error(messageTemplate, arg1, arg2, arg3, arg4);
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      wrapped.error(messageTemplate, arg1, arg2, arg3, arg4, arg5);
   }

   @Override
   public void error(final Throwable ex) {
      wrapped.error(ex);
   }

   @Override
   public void error(final Throwable ex, final String msg) {
      wrapped.error(ex, msg);
   }

   @Override
   public void error(final Throwable ex, final String messageTemplate, final Object... args) {
      wrapped.error(ex, messageTemplate, args);
   }

   @Override
   public void fatal(final Throwable ex) {
      wrapped.fatal(ex);
   }

   @Override
   public void fatal(final Throwable ex, final String msg) {
      wrapped.fatal(ex, msg);
   }

   @Override
   public void fatal(final Throwable ex, final String messageTemplate, final Object... args) {
      wrapped.fatal(ex, messageTemplate, args);
   }

   @Override
   public String getName() {
      return wrapped.getName();
   }

   @Override
   public void info(final String msg) {
      wrapped.info(msg);
   }

   @Override
   public void info(final String messageTemplate, final Object arg) {
      wrapped.info(messageTemplate, arg);
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2) {
      wrapped.info(messageTemplate, arg1, arg2);
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      wrapped.info(messageTemplate, arg1, arg2, arg3);
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      wrapped.info(messageTemplate, arg1, arg2, arg3, arg4);
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      wrapped.info(messageTemplate, arg1, arg2, arg3, arg4, arg5);
   }

   @Override
   public void info(final Throwable ex) {
      wrapped.info(ex);
   }

   @Override
   public void info(final Throwable ex, final String msg) {
      wrapped.info(ex, msg);
   }

   @Override
   public void info(final Throwable ex, final String messageTemplate, final Object... args) {
      wrapped.info(ex, messageTemplate, args);
   }

   @Override
   public void infoNew(final Object newInstance) {
      wrapped.infoNew(newInstance);
   }

   @Override
   public boolean isDebugEnabled() {
      return wrapped.isDebugEnabled();
   }

   @Override
   public boolean isErrorEnabled() {
      return wrapped.isErrorEnabled();
   }

   @Override
   public boolean isInfoEnabled() {
      return wrapped.isInfoEnabled();
   }

   @Override
   public boolean isTraceEnabled() {
      return wrapped.isTraceEnabled();
   }

   @Override
   public boolean isWarnEnabled() {
      return wrapped.isWarnEnabled();
   }

   @Override
   public String toString() {
      return super.toString() + "[name=" + getName() + ",delegate=" + wrapped + "]";
   }

   @Override
   public void trace(final String msg) {
      wrapped.trace(msg);
   }

   @Override
   public void trace(final String messageTemplate, final Object arg) {
      wrapped.trace(messageTemplate, arg);
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2) {
      wrapped.trace(messageTemplate, arg1, arg2);
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      wrapped.trace(messageTemplate, arg1, arg2, arg3);
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      wrapped.trace(messageTemplate, arg1, arg2, arg3, arg4);
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      wrapped.trace(messageTemplate, arg1, arg2, arg3, arg4, arg5);
   }

   @Override
   public void trace(final Throwable ex) {
      wrapped.trace(ex);
   }

   @Override
   public void trace(final Throwable ex, final String msg) {
      wrapped.trace(ex, msg);
   }

   @Override
   public void trace(final Throwable ex, final String messageTemplate, final Object... args) {
      wrapped.trace(ex, messageTemplate, args);
   }

   @Override
   public void entry() {
      wrapped.entry();
   }

   @Override
   public void entry(final Object arg1) {
      wrapped.entry(arg1);
   }

   @Override
   public void entry(final Object arg1, final Object arg2) {
      wrapped.entry(arg1, arg2);
   }

   @Override
   public void entry(final Object arg1, final Object arg2, final Object arg3) {
      wrapped.entry(arg1, arg2, arg3);
   }

   @Override
   public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      wrapped.entry(arg1, arg2, arg3, arg4);
   }

   @Override
   public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      wrapped.entry(arg1, arg2, arg3, arg4, arg5);
   }

   @Override
   public void exit() {
      wrapped.exit();
   }

   @Override
   public <T> T exit(final T returnValue) {
      return wrapped.exit(returnValue);
   }

   @Override
   public void warn(final String msg) {
      wrapped.warn(msg);
   }

   @Override
   public void warn(final String messageTemplate, final Object arg) {
      wrapped.warn(messageTemplate, arg);
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2) {
      wrapped.warn(messageTemplate, arg1, arg2);
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      wrapped.warn(messageTemplate, arg1, arg2, arg3);
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      wrapped.warn(messageTemplate, arg1, arg2, arg3, arg4);
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      wrapped.warn(messageTemplate, arg1, arg2, arg3, arg4, arg5);
   }

   @Override
   public void warn(final Throwable ex) {
      wrapped.warn(ex);
   }

   @Override
   public void warn(final Throwable ex, final String msg) {
      wrapped.warn(ex, msg);
   }

   @Override
   public void warn(final Throwable ex, final String messageTemplate, final Object... args) {
      wrapped.warn(ex, messageTemplate, args);
   }

   @Override
   public void trace(final Method location, final String msg) {
      wrapped.trace(location, msg);
   }
}
