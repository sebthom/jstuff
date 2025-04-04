/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.jdt.annotation.NonNull;

import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Beans extends java.beans.Beans {
   private static final WeakHashMap<Class<?>, Map<String, PropertyDescriptor>> BEAN_PROPERTIES_CACHE = new WeakHashMap<>();

   /**
    * list of properties
    *
    * @return an unmodifiable collection
    */
   public static Map<String, PropertyDescriptor> getBeanProperties(final Class<?> beanType) {
      Args.notNull("beanType", beanType);

      final Map<String, PropertyDescriptor> properties = BEAN_PROPERTIES_CACHE.get(beanType);
      if (properties == null)
         return populateCache(beanType);
      return properties;
   }

   /**
    * @return list of properties
    */
   public static Collection<PropertyDescriptor> getBeanPropertyDescriptors(final Class<?> beanType) {
      Args.notNull("beanType", beanType);

      final Map<String, PropertyDescriptor> properties = BEAN_PROPERTIES_CACHE.get(beanType);
      if (properties == null)
         return populateCache(beanType).values();
      return properties.values();
   }

   public static Set<String> getBeanPropertyNames(final Class<?> beanType) {
      Args.notNull("beanType", beanType);

      final Map<String, PropertyDescriptor> properties = BEAN_PROPERTIES_CACHE.get(beanType);
      if (properties == null)
         return populateCache(beanType).keySet();
      return properties.keySet();
   }

   private static Map<String, PropertyDescriptor> populateCache(final Class<?> beanType) {
      try {
         final BeanInfo beanInfo = Introspector.getBeanInfo(beanType, Object.class);
         final PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
         final var beanProperties = new HashMap<String, PropertyDescriptor>(props.length);
         for (final PropertyDescriptor prop : props) {
            final var name = prop.getName();
            if (name != null) {
               beanProperties.put(name, prop);
            }
         }
         final var map = Collections.unmodifiableMap(beanProperties);
         BEAN_PROPERTIES_CACHE.put(beanType, map);
         return map;
      } catch (final IntrospectionException ex) {
         throw new ReflectionException(ex);
      }
   }

   public static Object valueOf(final String stringValue, final Class<?> targetType) {
      Args.notNull("targetType", targetType);

      if (int.class.isAssignableFrom(targetType) || Integer.class.isAssignableFrom(targetType))
         return Integer.valueOf(stringValue);
      if (float.class.isAssignableFrom(targetType) || Float.class.isAssignableFrom(targetType))
         return Float.valueOf(stringValue);
      if (double.class.isAssignableFrom(targetType) || Double.class.isAssignableFrom(targetType))
         return Double.valueOf(stringValue);
      if (short.class.isAssignableFrom(targetType) || Short.class.isAssignableFrom(targetType))
         return Short.valueOf(stringValue);
      if (byte.class.isAssignableFrom(targetType) || Byte.class.isAssignableFrom(targetType))
         return Byte.valueOf(stringValue);
      if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType))
         return Boolean.valueOf(stringValue);
      return stringValue;
   }

   public static Object[] valuesOf(final @NonNull String[] stringValues, final @NonNull Class<?>[] targetTypes) {
      Assert.isTrue(stringValues.length == targetTypes.length, "Arguments [stringValues} and [targetTypes] must have the same length");

      final var result = new Object[targetTypes.length];

      for (int i = 0, l = targetTypes.length; i < l; i++) {
         result[i] = valueOf(stringValues[i], targetTypes[i]);
      }
      return result;
   }
}
