/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.ParameterizedType;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ClassVisitorWithTypeArguments
{
	/**
	 * @return false if the class hierarchy visit shall be aborted
	 */
	boolean visit(final Class< ? > clazz, final ParameterizedType type);

	/**
	 * @return if the class shall be visited
	 */
	boolean isVisiting(final Class< ? > clazz, final ParameterizedType type);

	/**
	 * @return if true the superclass shall be visited
	 */
	boolean isVisitingSuperclass(final Class< ? > clazz, final ParameterizedType type);

	/**
	 * @return if true the implemented interfaces shall be visited
	 */
	boolean isVisitingInterfaces(final Class< ? > clazz, final ParameterizedType type);
}