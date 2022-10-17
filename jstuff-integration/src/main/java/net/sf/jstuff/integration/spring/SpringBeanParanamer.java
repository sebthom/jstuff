/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import java.lang.reflect.Method;

import org.springframework.aop.support.AopUtils;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SpringBeanParanamer {
   private static final Logger LOG = Logger.create();

   private static final Paranamer PARANAMER = new CachingParanamer(new BytecodeReadingParanamer());

   public static String[] getParameterNames(final Method beanMethod, final Object bean) {
      if (beanMethod.getParameterTypes().length == 0)
         return ArrayUtils.EMPTY_STRING_ARRAY;

      String[] parameters = null;

      // try lookup the parameter based on the service interface's method declaration
      try {
         parameters = PARANAMER.lookupParameterNames(beanMethod);
      } catch (final ParameterNamesNotFoundException ex) {
         // expected
      }

      // check if the parameters could be found
      if (parameters != null && beanMethod.getParameterTypes().length == parameters.length)
         return parameters;

      // try lookup the parameter based on the service implementation's method declaration
      try {
         final Method serviceImplMethod = AopUtils.getTargetClass(bean).getMethod(beanMethod.getName(), beanMethod.getParameterTypes());
         parameters = PARANAMER.lookupParameterNames(serviceImplMethod);
         if (parameters != null)
            return parameters;
      } catch (final Exception ex) {
         LOG.trace("Unexpected exception", ex);
      }

      // as fallback use indexed parameter
      final var names = new String[beanMethod.getParameterTypes().length];
      for (int i = 0, l = beanMethod.getParameterTypes().length; i < l; i++) {
         names[i] = "param" + i;
      }
      return names;
   }
}
