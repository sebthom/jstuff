/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.ResultSetDynaClass;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BeanResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
   private static final Logger LOG = Logger.create();

   private Class<T> beanClass;
   private Map<String, String> beanPropertiesLowerCase;

   public BeanResultSetExtractor(final Class<T> beanClass) throws IntrospectionException {
      setBeanClass(beanClass);
   }

   @Override
   public List<T> extractData(final ResultSet resultSet) throws SQLException, DataAccessException {
      Assert.notNull(beanClass, "Property beanClass must be set.");

      final List<T> extractedBeans = new ArrayList<>();

      for (final Iterator<?> it = new ResultSetDynaClass(resultSet, true).iterator(); it.hasNext();) {
         final DynaBean dynaBean = (DynaBean) it.next();
         T bean = null;
         try {
            bean = this.getBeanClass().getDeclaredConstructor().newInstance();

            for (final DynaProperty dynaProp : dynaBean.getDynaClass().getDynaProperties()) {
               final String dynaPropName = dynaProp.getName();
               final Object dynaPropValue = dynaBean.get(dynaPropName);
               final String beanPropName = beanPropertiesLowerCase.get(dynaPropName);
               BeanUtils.copyProperty(bean, beanPropName, dynaPropValue);
            }
            extractedBeans.add(bean);
         } catch (final Exception ex) {
            LOG.error(ex, "Unexpected error occurred while processing DynaBean.\n beanClass=%s\n bean=%s\n dynaBean=%s", beanClass, Strings.toString(bean),
               Strings.toString(dynaBean));
         }
      }
      return extractedBeans;
   }

   public Class<T> getBeanClass() {
      return beanClass;
   }

   public void setBeanClass(final Class<T> beanClass) throws IntrospectionException {
      final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

      final HashMap<String, String> propsLowerCase = new HashMap<>();
      for (final PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
         final String propName = prop.getName();
         if (propsLowerCase.put(propName.toLowerCase(), propName) != null)
            throw new IllegalStateException("Bean Class " + beanClass.getName() + " contains multiple properties with same lowercase representation: "
               + propName);
      }

      this.beanClass = beanClass;
      this.beanPropertiesLowerCase = propsLowerCase;
   }
}
