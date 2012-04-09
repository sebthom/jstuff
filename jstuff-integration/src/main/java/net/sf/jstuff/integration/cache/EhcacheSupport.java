/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.integration.cache;

import javax.inject.Inject;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EhcacheSupport
{
	private CacheManager cacheManager;
	private Cache cache;
	private String cacheName;
	private final Object lock = new Object();

	public Cache getCache()
	{
		synchronized (lock)
		{
			if (cache == null) cache = cacheManager.getCache(cacheName);
			return cache;
		}
	}

	/**
	 * @param cacheManager the cacheManager to set
	 */
	@Inject
	public void setCacheManager(final CacheManager cacheManager)
	{
		synchronized (lock)
		{
			this.cacheManager = cacheManager;
			this.cache = null;
		}
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	@Inject
	public synchronized void setCacheName(final String cacheName)
	{
		synchronized (lock)
		{
			this.cacheName = cacheName;
			this.cache = null;
		}
	}
}
