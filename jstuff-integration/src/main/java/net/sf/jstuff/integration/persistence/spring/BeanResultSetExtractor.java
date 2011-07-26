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
package net.sf.jstuff.integration.persistence.spring;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.ResultSetDynaClass;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BeanResultSetExtractor<T> implements ResultSetExtractor<List<T>>
{
	private final static Logger LOG = Logger.make();

	private Class<T> beanClass;
	private Map<String, String> beanPropertiesLowerCase;

	public BeanResultSetExtractor(final Class<T> beanClass) throws IntrospectionException
	{
		setBeanClass(beanClass);
	}

	public List<T> extractData(final ResultSet resultSet) throws SQLException, DataAccessException
	{
		Assert.notNull(beanClass, "Property beanClass must be set.");

		final List<T> extractedBeans = new ArrayList<T>();

		for (final Iterator< ? > it = new ResultSetDynaClass(resultSet, true).iterator(); it.hasNext();)
		{
			final DynaBean dynaBean = (DynaBean) it.next();
			T bean = null;
			try
			{
				bean = this.getBeanClass().newInstance();

				for (final DynaProperty dynaProp : dynaBean.getDynaClass().getDynaProperties())
				{
					final String dynaPropName = dynaProp.getName();
					final Object dynaPropValue = dynaBean.get(dynaPropName);
					final String beanPropName = beanPropertiesLowerCase.get(dynaPropName);
					BeanUtils.copyProperty(bean, beanPropName, dynaPropValue);
				}
				extractedBeans.add(bean);
			}
			catch (final Exception ex)
			{
				LOG.error(
						"Unexpected error occurred while processing DynaBean.\n beanClass=%s\n bean=%s\n dynaBean=%s",
						beanClass, StringUtils.toString(bean), StringUtils.toString(dynaBean), ex);
			}
		}
		return extractedBeans;
	}

	/**
	 * @return the beanClass
	 */
	public Class<T> getBeanClass()
	{
		return beanClass;
	}

	/**
	 * @param beanClass the beanClass to set
	 * @throws IntrospectionException 
	 */
	public synchronized void setBeanClass(final Class<T> beanClass) throws IntrospectionException
	{
		final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

		final HashMap<String, String> propsLowerCase = new HashMap<String, String>();
		for (final PropertyDescriptor prop : beanInfo.getPropertyDescriptors())
		{
			final String propName = prop.getName();
			if (propsLowerCase.put(propName.toLowerCase(), propName) != null)
				throw new IllegalStateException("Bean Class " + beanClass.getName()
						+ " contains multiple properties with same lowercase representation: " + propName);
		}

		this.beanClass = beanClass;
		this.beanPropertiesLowerCase = propsLowerCase;
	}
}