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
package net.sf.jstuff.integration.servlet;

import java.io.Serializable;

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
			if (sessionCount > 0) sessionCount--;
		}
	}

	private static final Logger LOG = Logger.create();

	private static final HttpSessionBindingListenerImpl LISTENER = new HttpSessionBindingListenerImpl();

	protected static int sessionCount = 0;

	public static int getSessionCount()
	{
		return sessionCount;
	}

	public CountingHttpSessionListener()
	{
		LOG.info("instantiated");
	}

	public void sessionCreated(final HttpSessionEvent se)
	{
		sessionCount++;
		se.getSession().setAttribute("SessionCounterHttpSessionBindingListener", LISTENER);
	}

	public void sessionDestroyed(final HttpSessionEvent se)
	{
		// we are not using this method as it is not correctly implemented in all servlet containers
		// some only call it when a session is explicitely invalidated but not when it expires,
		// others invoke it multiple times
		// => instead we use  HttpSessionBindingListener.valueUnbound which seems to be ok
	}
}