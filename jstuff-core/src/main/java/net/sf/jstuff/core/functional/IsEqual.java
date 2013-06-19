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
package net.sf.jstuff.core.functional;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface IsEqual<T>
{
	IsEqual<Object> DEFAULT = new IsEqual<Object>()
		{
			public boolean isEqual(final Object obj1, final Object obj2)
			{
				return ObjectUtils.equals(obj1, obj2);
			}
		};

	boolean isEqual(T obj1, T obj2);
}