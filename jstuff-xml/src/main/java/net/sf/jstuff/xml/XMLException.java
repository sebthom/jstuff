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
package net.sf.jstuff.xml;

/**
 * Wrapping runtime exception for checked XML related exceptions.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class XMLException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public XMLException(final Throwable cause)
	{
		super(cause);
	}
}