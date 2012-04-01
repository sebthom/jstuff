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
package net.sf.jstuff.integration.json;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jstuff.core.reflection.BeanUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ExtJSUtils
{
	public static List<String> getRecordDefinition(final Class< ? > javaBeanType)
	{
		final Collection<PropertyDescriptor> props = BeanUtils.getBeanPropertyDescriptors(javaBeanType);
		final List<String> recordDef = new ArrayList<String>(props.size());
		final StringBuilder sb = new StringBuilder();
		for (final PropertyDescriptor prop : props)
		{
			sb.append('{');
			sb.append("name:\"");
			sb.append(prop.getName());
			final Class< ? > clazz = prop.getPropertyType();
			sb.append("\",type:\"");
			sb.append(getRecordFieldType(clazz));
			sb.append("\"");
			sb.append('}');
			recordDef.add(sb.toString());
			sb.setLength(0);
		}
		return recordDef;
	}

	public static String getRecordFieldType(final Class< ? > fieldType)
	{
		if (String.class.isAssignableFrom(fieldType) || Character.class.isAssignableFrom(fieldType)
				|| char.class.isAssignableFrom(fieldType))
			return "string";
		else if (Integer.class.isAssignableFrom(fieldType) || Long.class.isAssignableFrom(fieldType)
				|| Short.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)
				|| long.class.isAssignableFrom(fieldType) || short.class.isAssignableFrom(fieldType)
				|| BigInteger.class.isAssignableFrom(fieldType) || Byte.class.isAssignableFrom(fieldType)
				|| byte.class.isAssignableFrom(fieldType))
			return "int";
		else if (Double.class.isAssignableFrom(fieldType) || Float.class.isAssignableFrom(fieldType)
				|| BigDecimal.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)
				|| float.class.isAssignableFrom(fieldType))
			return "float";
		else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))
			return "boolean";
		else if (java.util.Date.class.isAssignableFrom(fieldType))
			return "date";
		else
			return "auto";
	}

	protected ExtJSUtils()
	{
		super();
	}
}
