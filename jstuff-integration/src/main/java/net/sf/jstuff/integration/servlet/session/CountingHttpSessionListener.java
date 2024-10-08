/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet.session;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CountingHttpSessionListener implements HttpSessionListener {
   private static final class HttpSessionBindingListenerImpl implements HttpSessionBindingListener, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public void valueBound(final HttpSessionBindingEvent ev) {
         // nothing to do
      }

      @Override
      public void valueUnbound(final HttpSessionBindingEvent ev) {
         SESSION_COUNT.decrementAndGet();
      }
   }

   private static final Logger LOG = Logger.create();

   private static final HttpSessionBindingListenerImpl LISTENER = new HttpSessionBindingListenerImpl();
   private static final AtomicInteger SESSION_COUNT = new AtomicInteger();

   public static int getSessionCount() {
      return SESSION_COUNT.intValue();
   }

   public CountingHttpSessionListener() {
      LOG.infoNew(this);
   }

   @Override
   public void sessionCreated(final HttpSessionEvent se) {
      SESSION_COUNT.incrementAndGet();
      se.getSession().setAttribute(CountingHttpSessionListener.class.getName(), LISTENER);
   }

   /**
    * We are not using this method since it is not correctly implemented by all servlet containers.
    * Some only call it when a session is explicitly invalidated but not when it expires,
    * others invoke it multiple times.
    * Instead we use {@link HttpSessionBindingListener#valueUnbound} which seems to work reliable
    */

   @Override
   public void sessionDestroyed(final HttpSessionEvent se) {
      //do nothing
   }
}
