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
package net.sf.jstuff.core.concurrent;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuntimeInterruptedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public RuntimeInterruptedException(final InterruptedException cause)
	{
		super(cause);
		Args.notNull("cause", cause);
	}

	public RuntimeInterruptedException(final String message, final InterruptedException cause)
	{
		super(message, cause);
		Args.notNull("message", message);
		Args.notNull("cause", cause);
	}

	/**
	 * {@docRoot}
	 */
	@Override
	public InterruptedException getCause()
	{
		return (InterruptedException) super.getCause();
	}
}
