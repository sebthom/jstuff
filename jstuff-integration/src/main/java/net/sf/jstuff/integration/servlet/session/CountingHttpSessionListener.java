/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.jstuff.integration.servlet.session;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CountingHttpSessionListener implements HttpSessionListener
{
	private static final class HttpSessionBindingListenerImpl implements HttpSessionBindingListener, Serializable
	{
		private static final long serialVersionUID = 1L;

		public void valueBound(final HttpSessionBindingEvent ev)
		{}

		public void valueUnbound(final HttpSessionBindingEvent ev)
		{
			SESSION_COUNT.decrementAndGet();
		}
	}

	private static final Logger LOG = Logger.create();

	private static final HttpSessionBindingListenerImpl LISTENER = new HttpSessionBindingListenerImpl();
	private static final AtomicInteger SESSION_COUNT = new AtomicInteger();

	public static int getSessionCount()
	{
		return SESSION_COUNT.intValue();
	}

	public CountingHttpSessionListener()
	{
		LOG.info("instantiated");
	}

	public void sessionCreated(final HttpSessionEvent se)
	{
		SESSION_COUNT.incrementAndGet();
		se.getSession().setAttribute(CountingHttpSessionListener.class.getName(), LISTENER);
	}

	/**
	 * We are not using this method since it is not correctly implemented by all servlet containers.
	 * Some only call it when a session is explicitly invalidated but not when it expires,
	 * others invoke it multiple times.
	 * Instead we use {@link HttpSessionBindingListener#valueUnbound} which seems to work reliable
	 */
	public void sessionDestroyed(final HttpSessionEvent se)
	{
		//do nothing
	}
}