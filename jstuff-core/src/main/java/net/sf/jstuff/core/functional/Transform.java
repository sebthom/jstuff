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
package net.sf.jstuff.core.functional;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Transform<From, To>
{
	<NextTo> Transform<From, NextTo> and(final Transform< ? super To, NextTo> next);

	To transform(From source);
}
