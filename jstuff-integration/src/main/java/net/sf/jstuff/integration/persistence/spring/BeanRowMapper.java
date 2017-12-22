/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.persistence.spring;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.ResultSetDynaClass;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.jdbc.core.RowMapper;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Class represent a mapper that map column values of rows to bean properties
 * This implementation use schema: columnname(lowercase) --> bean property name
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BeanRowMapper<T> implements RowMapper<T> {
    private static final Logger LOG = Logger.create();

    private Class<T> beanClass;

    private static final BeanUtilsBean BUB;

    static {
        /**
         * to avoid
         * org.apache.commons.beanutils.ConversionException: No value specified for 'Date'
         * at org.apache.commons.beanutils.converters.AbstractConverter.handleMissing(AbstractConverter.java:325)
         * at org.apache.commons.beanutils.converters.AbstractConverter.convert(AbstractConverter.java:151)
         * at org.apache.commons.beanutils.converters.ConverterFacade.convert(ConverterFacade.java:60)
         * at org.apache.commons.beanutils.BeanUtilsBean.convert(BeanUtilsBean.java:1079)
         * at org.apache.commons.beanutils.BeanUtilsBean.copyProperty(BeanUtilsBean.java:437)
         * at org.apache.commons.beanutils.BeanUtils.copyProperty(BeanUtils.java:160)
         */
        final ConvertUtilsBean converter = new ConvertUtilsBean();
        converter.register(new DateConverter(null), Date.class);
        BUB = new BeanUtilsBean(converter);
    }

    /**
     * propertyNameLowerCase => propertyName
     */
    private Map<String, String> beanPropertyNames;

    private final WeakHashMap<ResultSet, ResultSetDynaClass> rsDynaClassesCache = new WeakHashMap<ResultSet, ResultSetDynaClass>();

    public BeanRowMapper(final Class<T> beanClass) throws IntrospectionException {
        Args.notNull("beanClass", beanClass);

        setBeanClass(beanClass);
    }

    /**
     * @return the beanClass
     */
    public Class<T> getBeanClass() {
        return beanClass;
    }

    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        ResultSetDynaClass rsDynaClass = rsDynaClassesCache.get(rs);
        if (rsDynaClass == null) {
            rsDynaClass = new ResultSetDynaClass(rs, true);
            rsDynaClassesCache.put(rs, rsDynaClass);
        }

        try {
            // generate bean instance
            final T bean = this.getBeanClass().newInstance();
            //copy property from result set to created bean
            for (final DynaProperty dynaProp : rsDynaClass.getDynaProperties()) {
                final String dynaPropName = dynaProp.getName();
                final Object dynaPropValue = rsDynaClass.getObjectFromResultSet(dynaPropName);
                final String beanPropName = beanPropertyNames.get(dynaPropName);
                BUB.copyProperty(bean, beanPropName, dynaPropValue);
            }

            return bean;
        } catch (final IllegalAccessException ex) {
            LOG.error(ex);
            throw new SQLException(ex.getMessage());
        } catch (final InstantiationException ex) {
            LOG.error(ex);
            throw new SQLException(ex.getMessage());
        } catch (final InvocationTargetException ex) {
            LOG.error(ex);
            throw new SQLException(ex.getMessage());
        }
    }

    /**
     * Method set beanClass and read information from the bean and to create internal mapping table
     * for properties ( propertyNameLowerCase => propertyName )
     *
     * @param beanClass the beanClass to set
     * @throws IntrospectionException
     */
    protected void setBeanClass(final Class<T> beanClass) throws IntrospectionException {
        // get beanInformation from bean e.g. property names
        final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

        final HashMap<String, String> propsLowerCase = new HashMap<String, String>();
        for (final PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
            final String propName = prop.getName();
            if (propsLowerCase.put(propName.toLowerCase(), propName) != null)
                throw new IllegalStateException("Bean Class " + beanClass.getName() + " contains multiple properties with same lowercase representation: "
                        + propName);
        }

        this.beanClass = beanClass;
        this.beanPropertyNames = propsLowerCase;
    }
}