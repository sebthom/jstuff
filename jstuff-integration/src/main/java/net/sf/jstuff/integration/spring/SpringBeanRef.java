/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import java.io.Serializable;

import net.sf.jstuff.core.validation.Args;

/**
 * A serializable reference to a Spring managed bean. Relies on a configured {@link SpringBeanLocator}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SpringBeanRef<T> implements Serializable {
   private static final long serialVersionUID = 1L;

   public static <T> SpringBeanRef<T> of(final Class<T> beanType) {
      return new SpringBeanRef<>(beanType);
   }

   public static <T> SpringBeanRef<T> of(final String beanName) {
      return new SpringBeanRef<>(beanName);
   }

   private transient T springBean;

   private final String beanName;

   private final Class<T> beanType;

   private SpringBeanRef(final Class<T> beanType) {
      Args.notNull("beanType", beanType);
      this.beanType = beanType;
      beanName = null;
   }

   private SpringBeanRef(final String beanName) {
      Args.notNull("beanName", beanName);
      this.beanName = beanName;
      beanType = null;
   }

   @SuppressWarnings("unchecked")
   public T get() {
      if (springBean == null) {
         springBean = (T) (beanName == null ? SpringBeanLocator.get().byClass(beanType) : SpringBeanLocator.get().byName(beanName));
      }
      return springBean;
   }
}
