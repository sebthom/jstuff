/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.integration.rest;

import java.io.Serializable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RESTServiceError implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final String message;
	private final String type;

	public RESTServiceError(final String type, final String message)
	{
		this.type = type;
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public String getType()
	{
		return type;
	}
}