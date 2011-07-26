/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.jstuff.core.reflection;

import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SetAccessibleAction implements PrivilegedAction<Object>
{
	private final AccessibleObject ao;

	private boolean accessible = true;

	public SetAccessibleAction(final AccessibleObject ao)
	{
		this.ao = ao;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object run()
	{
		ao.setAccessible(accessible);
		return null;
	}

	public SetAccessibleAction setAccessible(final boolean accessible)
	{
		this.accessible = accessible;
		return this;
	}
}