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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ClassVisitor
{
	boolean isVisitingFields(Class< ? > clazz);

	boolean isVisitingField(Field field);

	/**
	 * @return false if the class hierarchy visit shall be aborted
	 */
	boolean visit(Field field);

	boolean isVisitingMethods(Class< ? > clazz);

	boolean isVisitingMethod(Method method);

	/**
	 * @return false if the class hierarchy visit shall be aborted
	 */
	boolean visit(Method method);

	/**
	* @return false if the class hierarchy visit shall be aborted
	*/
	boolean visit(Class< ? > clazz);

	/**
	 * @return if the class shall be visited
	 */
	boolean isVisiting(Class< ? > clazz);

	/**
	 * @return if true the superclass shall be visited
	 */
	boolean isVisitingSuperclass(Class< ? > clazz);

	/**
	 * @return if true the implemented interfaces shall be visited
	 */
	boolean isVisitingInterfaces(Class< ? > clazz);
}