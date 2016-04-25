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

    public String getContextPath() {
        return delegate.getContextPath();
    }

    public ServletContext getContext(final String uripath) {
        return delegate.getContext(uripath);
    }

    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }

    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }

    public String getMimeType(final String file) {
        return delegate.getMimeType(file);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getResourcePaths(final String path) {
        return delegate.getResourcePaths(path);
    }

    public URL getResource(final String path) throws MalformedURLException {
        return delegate.getResource(path);
    }

    public InputStream getResourceAsStream(final String path) {
        return delegate.getResourceAsStream(path);
    }

    public RequestDispatcher getRequestDispatcher(final String path) {
        return delegate.getRequestDispatcher(path);
    }

    public RequestDispatcher getNamedDispatcher(final String name) {
        return delegate.getNamedDispatcher(name);
    }

    @SuppressWarnings({ "deprecation" })
    public Servlet getServlet(final String name) throws ServletException {
        return delegate.getServlet(name);
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    public Enumeration getServlets() {
        return delegate.getServlets();
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    public Enumeration getServletNames() {
        return delegate.getServletNames();
    }

    public void log(final String msg) {
        delegate.log(msg);
    }

    @SuppressWarnings("deprecation")
    public void log(final Exception exception, final String msg) {
        delegate.log(exception, msg);
    }

    public void log(final String message, final Throwable throwable) {
        delegate.log(message, throwable);
    }

    public String getRealPath(final String path) {
        return delegate.getRealPath(path);
    }

    public String getServerInfo() {
        return delegate.getServerInfo();
    }

    public String getInitParameter(final String name) {
        return delegate.getInitParameter(name);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getInitParameterNames() {
        return delegate.getInitParameterNames();
    }

    public Object getAttribute(final String name) {
        return delegate.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    public void setAttribute(final String name, final Object object) {
        delegate.setAttribute(name, object);
    }

    public void removeAttribute(final String name) {
        delegate.removeAttribute(name);
    }

    public String getServletContextName() {
        return delegate.getServletContextName();
    }
}
