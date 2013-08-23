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
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ClassVisitor
{
	boolean isVisitingFields(final Class< ? > clazz);

	boolean isVisitingField(final Field field);

	/**
	 * @return false if the class hierarchy visit shall be aborted
	 */
	boolean visit(final Field field);

	boolean isVisitingMethods(final Class< ? > clazz);

	boolean isVisitingMethod(final Method method);

	/**
	 * @return false if the class hierarchy visit shall be aborted
	 */
	boolean visit(final Method method);

	/**
	* @return false if the class hierarchy visit shall be aborted
	*/
	boolean visit(final Class< ? > clazz);

	/**
	 * @return if the class shall be visited
	 */
	boolean isVisiting(final Class< ? > clazz);

	/**
	 * @return if true the superclass shall be visited
	 */
	boolean isVisitingSuperclass(final Class< ? > clazz);

	/**
	 * @return if true the implemented interfaces shall be visited
	 */
	boolean isVisitingInterfaces(final Class< ? > clazz);
}