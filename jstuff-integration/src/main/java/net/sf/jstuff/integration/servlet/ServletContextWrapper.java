/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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

   public ServletContextWrapper(final ServletContext delegate) {
      Args.notNull("delegate", delegate);
      this.delegate = delegate;
   }

   public Object getAttribute(final String name) {
      return delegate.getAttribute(name);
   }

   @SuppressWarnings("unchecked")
   public Enumeration<String> getAttributeNames() {
      return delegate.getAttributeNames();
   }

   public ServletContext getContext(final String uripath) {
      return delegate.getContext(uripath);
   }

   public String getContextPath() {
      return delegate.getContextPath();
   }

   public ServletContext getDelegate() {
      return delegate;
   }

   public String getInitParameter(final String name) {
      return delegate.getInitParameter(name);
   }

   @SuppressWarnings("unchecked")
   public Enumeration<String> getInitParameterNames() {
      return delegate.getInitParameterNames();
   }

   public int getMajorVersion() {
      return delegate.getMajorVersion();
   }

   public String getMimeType(final String file) {
      return delegate.getMimeType(file);
   }

   public int getMinorVersion() {
      return delegate.getMinorVersion();
   }

   public RequestDispatcher getNamedDispatcher(final String name) {
      return delegate.getNamedDispatcher(name);
   }

   public String getRealPath(final String path) {
      return delegate.getRealPath(path);
   }

   public RequestDispatcher getRequestDispatcher(final String path) {
      return delegate.getRequestDispatcher(path);
   }

   public URL getResource(final String path) throws MalformedURLException {
      return delegate.getResource(path);
   }

   public InputStream getResourceAsStream(final String path) {
      return delegate.getResourceAsStream(path);
   }

   @SuppressWarnings("unchecked")
   public Set<String> getResourcePaths(final String path) {
      return delegate.getResourcePaths(path);
   }

   public String getServerInfo() {
      return delegate.getServerInfo();
   }

   @SuppressWarnings({"deprecation"})
   public Servlet getServlet(final String name) throws ServletException {
      return delegate.getServlet(name);
   }

   public String getServletContextName() {
      return delegate.getServletContextName();
   }

   @SuppressWarnings({"deprecation", "rawtypes"})
   public Enumeration getServletNames() {
      return delegate.getServletNames();
   }

   @SuppressWarnings({"deprecation", "rawtypes"})
   public Enumeration getServlets() {
      return delegate.getServlets();
   }

   @SuppressWarnings("deprecation")
   public void log(final Exception exception, final String msg) {
      delegate.log(exception, msg);
   }

   public void log(final String msg) {
      delegate.log(msg);
   }

   public void log(final String message, final Throwable throwable) {
      delegate.log(message, throwable);
   }

   public void removeAttribute(final String name) {
      delegate.removeAttribute(name);
   }

   public void setAttribute(final String name, final Object object) {
      delegate.setAttribute(name, object);
   }

   public void setDelegate(final ServletContext delegate) {
      Args.notNull("delegate", delegate);
      this.delegate = delegate;
   }
}
