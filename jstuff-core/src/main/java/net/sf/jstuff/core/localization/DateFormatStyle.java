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
package net.sf.jstuff.core.localization;

import java.text.DateFormat;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public enum DateFormatStyle
{
	FULL(DateFormat.FULL), //
	LONG(DateFormat.LONG), //
	MEDIUM(DateFormat.MEDIUM), //
	SHORT(DateFormat.SHORT);

	public final int style;

	private DateFormatStyle(final int style)
	{
		this.style = style;
	}
}