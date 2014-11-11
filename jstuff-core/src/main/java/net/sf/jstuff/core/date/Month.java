/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.core.date;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public enum Month
{
	JANUARY,
	FEBRUAR,
	MARCH,
	APRIL,
	MAY,
	JUNE,
	JULY,
	AUGUST,
	SEPTEMBER,
	OCTOBER,
	NOVEMBER,
	DECEMBER;

	private static final String BUNDLE_NAME = Month.class.getName();

	@Override
	public String toString()
	{
		try
		{
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(name());
		}
		catch (final MissingResourceException e)
		{
			return name();
		}
	}
}
