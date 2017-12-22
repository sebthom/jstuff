/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServletContextWrapper implements ServletContext {
    protected ServletContext delegate;

    public ServletContext getDelegate() {
        return delegate;
    }

    public void setDelegate(final ServletContext delegate) {
        Args.notNull("delegate", delegate);
        this.delegate = delegate;
    }

    public ServletContextWrapper(final ServletContext delegate) {
        Args.notNull("delegate", delegate);
        this.delegate = delegate;
    }

    @Override
    public String getContextPath() {
        return delegate.getContextPath();
    }

    @Override
    public ServletContext getContext(final String uripath) {
        return delegate.getContext(uripath);
    }

    @Override
    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }

    @Override
    public String getMimeType(final String file) {
        return delegate.getMimeType(file);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getResourcePaths(final String path) {
        return delegate.getResourcePaths(path);
    }

    @Override
    public URL getResource(final String path) throws MalformedURLException {
        return delegate.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(final String path) {
        return delegate.getResourceAsStream(path);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        return delegate.getRequestDispatcher(path);
    }

    @Override
    public RequestDispatcher getNamedDispatcher(final String name) {
        return delegate.getNamedDispatcher(name);
    }

    @Override
    @SuppressWarnings({ "deprecation" })
    public Servlet getServlet(final String name) throws ServletException {
        return delegate.getServlet(name);
    }

    @Override
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public Enumeration getServlets() {
        return delegate.getServlets();
    }

    @Override
    @SuppressWarnings({ "deprecation", "rawtypes" })
    public Enumeration getServletNames() {
        return delegate.getServletNames();
    }

    @Override
    public void log(final String msg) {
        delegate.log(msg);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void log(final Exception exception, final String msg) {
        delegate.log(exception, msg);
    }

    @Override
    public void log(final String message, final Throwable throwable) {
        delegate.log(message, throwable);
    }

    @Override
    public String getRealPath(final String path) {
        return delegate.getRealPath(path);
    }

    @Override
    public String getServerInfo() {
        return delegate.getServerInfo();
    }

    @Override
    public String getInitParameter(final String name) {
        return delegate.getInitParameter(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<String> getInitParameterNames() {
        return delegate.getInitParameterNames();
    }

    @Override
    public Object getAttribute(final String name) {
        return delegate.getAttribute(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<String> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    @Override
    public void setAttribute(final String name, final Object object) {
        delegate.setAttribute(name, object);
    }

    @Override
    public void removeAttribute(final String name) {
        delegate.removeAttribute(name);
    }

    @Override
    public String getServletContextName() {
        return delegate.getServletContextName();
    }
}
