/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServletContextWrapper implements ServletContext {
   protected ServletContext delegate;

   public ServletContextWrapper(final ServletContext delegate) {
      Args.notNull("delegate", delegate);
      this.delegate = delegate;
   }

   @Override
   public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass) {
      return delegate.addFilter(filterName, filterClass);
   }

   @Override
   public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter) {
      return delegate.addFilter(filterName, filter);
   }

   @Override
   public FilterRegistration.Dynamic addFilter(final String filterName, final String className) {
      return delegate.addFilter(filterName, className);
   }

   @Override
   public Dynamic addJspFile(final String servletName, final String jspFile) {
      return delegate.addJspFile(servletName, jspFile);
   }

   @Override
   public void addListener(final Class<? extends EventListener> listenerClass) {
      delegate.addListener(listenerClass);
   }

   @Override
   public void addListener(final String className) {
      delegate.addListener(className);
   }

   @Override
   public <T extends EventListener> void addListener(final T t) {
      delegate.addListener(t);
   }

   @Override
   public Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass) {
      return delegate.addServlet(servletName, servletClass);
   }

   @Override
   public Dynamic addServlet(final String servletName, final Servlet servlet) {
      return delegate.addServlet(servletName, servlet);
   }

   @Override
   public Dynamic addServlet(final String servletName, final String className) {
      return delegate.addServlet(servletName, className);
   }

   @Override
   public <T extends Filter> T createFilter(final Class<T> clazz) throws ServletException {
      return delegate.createFilter(clazz);
   }

   @Override
   public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException {
      return delegate.createListener(clazz);
   }

   @Override
   public <T extends Servlet> T createServlet(final Class<T> clazz) throws ServletException {
      return delegate.createServlet(clazz);
   }

   @Override
   public void declareRoles(final String... roleNames) {
      delegate.declareRoles(roleNames);
   }

   @Override
   public @Nullable Object getAttribute(final String name) {
      return delegate.getAttribute(name);
   }

   @Override
   public Enumeration<String> getAttributeNames() {
      return delegate.getAttributeNames();
   }

   @Override
   public ClassLoader getClassLoader() {
      return delegate.getClassLoader();
   }

   @Override
   public @Nullable ServletContext getContext(final String uripath) {
      return delegate.getContext(uripath);
   }

   @Override
   public String getContextPath() {
      return delegate.getContextPath();
   }

   @Override
   public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
      return delegate.getDefaultSessionTrackingModes();
   }

   public ServletContext getDelegate() {
      return delegate;
   }

   @Override
   public int getEffectiveMajorVersion() {
      return delegate.getEffectiveMajorVersion();
   }

   @Override
   public int getEffectiveMinorVersion() {
      return delegate.getEffectiveMinorVersion();
   }

   @Override
   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
      return delegate.getEffectiveSessionTrackingModes();
   }

   @Override
   public @Nullable FilterRegistration getFilterRegistration(final String filterName) {
      return delegate.getFilterRegistration(filterName);
   }

   @Override
   public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
      return delegate.getFilterRegistrations();
   }

   @Override
   public @Nullable String getInitParameter(final String name) {
      return delegate.getInitParameter(name);
   }

   @Override
   public Enumeration<String> getInitParameterNames() {
      return delegate.getInitParameterNames();
   }

   @Override
   public JspConfigDescriptor getJspConfigDescriptor() {
      return delegate.getJspConfigDescriptor();
   }

   @Override
   public int getMajorVersion() {
      return delegate.getMajorVersion();
   }

   @Override
   public @Nullable String getMimeType(final String file) {
      return delegate.getMimeType(file);
   }

   @Override
   public int getMinorVersion() {
      return delegate.getMinorVersion();
   }

   @Override
   public @Nullable RequestDispatcher getNamedDispatcher(final String name) {
      return delegate.getNamedDispatcher(name);
   }

   @Override
   public @Nullable String getRealPath(final String path) {
      return delegate.getRealPath(path);
   }

   @Override
   public String getRequestCharacterEncoding() {
      return delegate.getRequestCharacterEncoding();
   }

   @Override
   public @Nullable RequestDispatcher getRequestDispatcher(final String path) {
      return delegate.getRequestDispatcher(path);
   }

   @Override
   public @Nullable URL getResource(final String path) throws MalformedURLException {
      return delegate.getResource(path);
   }

   @Override
   public @Nullable InputStream getResourceAsStream(final String path) {
      return delegate.getResourceAsStream(path);
   }

   @Override
   public @Nullable Set<String> getResourcePaths(final String path) {
      return delegate.getResourcePaths(path);
   }

   @Override
   public String getResponseCharacterEncoding() {
      return delegate.getResponseCharacterEncoding();
   }

   @Override
   public String getServerInfo() {
      return delegate.getServerInfo();
   }

   @Override
   public String getServletContextName() {
      return delegate.getServletContextName();
   }

   @Override
   public @Nullable ServletRegistration getServletRegistration(final String servletName) {
      return delegate.getServletRegistration(servletName);
   }

   @Override
   public Map<String, ? extends ServletRegistration> getServletRegistrations() {
      return delegate.getServletRegistrations();
   }

   @Override
   public SessionCookieConfig getSessionCookieConfig() {
      return delegate.getSessionCookieConfig();
   }

   @Override
   public int getSessionTimeout() {
      return delegate.getSessionTimeout();
   }

   @Override
   public String getVirtualServerName() {
      return delegate.getVirtualServerName();
   }

   @Override
   public void log(final @Nullable String msg) {
      delegate.log(msg);
   }

   @Override
   public void log(final @Nullable String message, final @Nullable Throwable throwable) {
      delegate.log(message, throwable);
   }

   @Override
   public void removeAttribute(final String name) {
      delegate.removeAttribute(name);
   }

   @Override
   public void setAttribute(final String name, final Object object) {
      delegate.setAttribute(name, object);
   }

   public void setDelegate(final ServletContext delegate) {
      Args.notNull("delegate", delegate);
      this.delegate = delegate;
   }

   @Override
   public boolean setInitParameter(final String name, final String value) {
      return delegate.setInitParameter(name, value);
   }

   @Override
   public void setRequestCharacterEncoding(final String encoding) {
      delegate.setRequestCharacterEncoding(encoding);
   }

   @Override
   public void setResponseCharacterEncoding(final String encoding) {
      delegate.setRequestCharacterEncoding(encoding);
   }

   @Override
   public void setSessionTimeout(final int sessionTimeout) {
      delegate.setSessionTimeout(sessionTimeout);
   }

   @Override
   public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {
      delegate.setSessionTrackingModes(sessionTrackingModes);
   }
}
