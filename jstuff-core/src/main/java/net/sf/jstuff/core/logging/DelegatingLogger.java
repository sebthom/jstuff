/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.logging;

import java.lang.reflect.Method;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
final class DelegatingLogger extends Logger {
    private Logger delegate;

    static final String FQCN = DelegatingLogger.class.getName();

    DelegatingLogger(final Logger delegate) {
        Args.notNull("delegate", delegate);
        this.delegate = delegate;
    }

    @Override
    public void debug(final String msg) {
        delegate.debug(msg);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg) {
        delegate.debug(messageTemplate, arg);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2) {
        delegate.debug(messageTemplate, arg1, arg2);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        delegate.debug(messageTemplate, arg1, arg2, arg3);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        delegate.debug(messageTemplate, arg1, arg2, arg3, arg4);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        delegate.debug(messageTemplate, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void debug(final Throwable ex) {
        delegate.debug(ex);
    }

    @Override
    public void debug(final Throwable ex, final String msg) {
        delegate.debug(ex, msg);
    }

    @Override
    public void debug(final Throwable ex, final String messageTemplate, final Object... args) {
        delegate.debug(ex, messageTemplate, args);
    }

    @Override
    public void error(final String msg) {
        delegate.error(msg);
    }

    @Override
    public void error(final String messageTemplate, final Object arg) {
        delegate.error(messageTemplate, arg);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2) {
        delegate.error(messageTemplate, arg1, arg2);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        delegate.error(messageTemplate, arg1, arg2, arg3);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        delegate.error(messageTemplate, arg1, arg2, arg3, arg4);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        delegate.error(messageTemplate, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void error(final Throwable ex) {
        delegate.error(ex);
    }

    @Override
    public void error(final Throwable ex, final String msg) {
        delegate.error(ex, msg);
    }

    @Override
    public void error(final Throwable ex, final String messageTemplate, final Object... args) {
        delegate.error(ex, messageTemplate, args);
    }

    @Override
    public void fatal(final Throwable ex) {
        delegate.fatal(ex);
    }

    @Override
    public void fatal(final Throwable ex, final String msg) {
        delegate.fatal(ex, msg);
    }

    @Override
    public void fatal(final Throwable ex, final String messageTemplate, final Object... args) {
        delegate.fatal(ex, messageTemplate, args);
    }

    Logger getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void info(final String msg) {
        delegate.info(msg);
    }

    @Override
    public void info(final String messageTemplate, final Object arg) {
        delegate.info(messageTemplate, arg);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2) {
        delegate.info(messageTemplate, arg1, arg2);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        delegate.info(messageTemplate, arg1, arg2, arg3);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        delegate.info(messageTemplate, arg1, arg2, arg3, arg4);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        delegate.info(messageTemplate, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void info(final Throwable ex) {
        delegate.info(ex);
    }

    @Override
    public void info(final Throwable ex, final String msg) {
        delegate.info(ex, msg);
    }

    @Override
    public void info(final Throwable ex, final String messageTemplate, final Object... args) {
        delegate.info(ex, messageTemplate, args);
    }

    @Override
    public void infoNew(final Object newInstance) {
        delegate.infoNew(newInstance);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    void setDelegate(final Logger delegate) {
        Args.notNull("delegate", delegate);
        this.delegate = delegate;
    }

    @Override
    public String toString() {
        return super.toString() + "[name=" + getName() + ",delegate=" + delegate + "]";
    }

    @Override
    protected void trace(final Method location, final String msg) {
        delegate.trace(location, msg);
    }

    @Override
    public void trace(final String msg) {
        delegate.trace(msg);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg) {
        delegate.trace(messageTemplate, arg);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2) {
        delegate.trace(messageTemplate, arg1, arg2);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        delegate.trace(messageTemplate, arg1, arg2, arg3);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        delegate.trace(messageTemplate, arg1, arg2, arg3, arg4);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        delegate.trace(messageTemplate, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void trace(final Throwable ex) {
        delegate.trace(ex);
    }

    @Override
    public void trace(final Throwable ex, final String msg) {
        delegate.trace(ex, msg);
    }

    @Override
    public void trace(final Throwable ex, final String messageTemplate, final Object... args) {
        delegate.trace(ex, messageTemplate, args);
    }

    @Override
    public void entry() {
        delegate.entry();
    }

    @Override
    public void entry(final Object arg1) {
        delegate.entry(arg1);
    }

    @Override
    public void entry(final Object arg1, final Object arg2) {
        delegate.entry(arg1, arg2);
    }

    @Override
    public void entry(final Object arg1, final Object arg2, final Object arg3) {
        delegate.entry(arg1, arg2, arg3);
    }

    @Override
    public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        delegate.entry(arg1, arg2, arg3, arg4);
    }

    @Override
    public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        delegate.entry(arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void exit() {
        delegate.exit();
    }

    @Override
    public <T> T exit(final T returnValue) {
        return delegate.exit(returnValue);
    }

    @Override
    public void warn(final String msg) {
        delegate.warn(msg);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg) {
        delegate.warn(messageTemplate, arg);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2) {
        delegate.warn(messageTemplate, arg1, arg2);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        delegate.warn(messageTemplate, arg1, arg2, arg3);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        delegate.warn(messageTemplate, arg1, arg2, arg3, arg4);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        delegate.warn(messageTemplate, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void warn(final Throwable ex) {
        delegate.warn(ex);
    }

    @Override
    public void warn(final Throwable ex, final String msg) {
        delegate.warn(ex, msg);
    }

    @Override
    public void warn(final Throwable ex, final String messageTemplate, final Object... args) {
        delegate.warn(ex, messageTemplate, args);
    }
}
