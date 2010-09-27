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
package net.sf.jstuff.core.reflection;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class InvokingMethodFailedException extends ReflectionException
{
	private static final long serialVersionUID = 1L;

	private final SerializableMethod method;
	private final transient Object targetObject;
	private final Serializable targetSerializableObject;

	public InvokingMethodFailedException(final Method method, final Object targetObject, final Throwable cause)
	{
		super("Invoking method " + method.getName() + " failed.", cause);
		this.method = SerializableMethod.get(method);
		this.targetObject = targetObject;
		targetSerializableObject = targetObject instanceof Serializable ? (Serializable) targetObject : null;
	}

	public Method getMethod()
	{
		return method.getMethod();
	}

	public Object getTargetObject()
	{
		return targetObject != null ? targetObject : targetSerializableObject;
	}
}