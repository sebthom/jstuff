/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.Serializable;

import org.eclipse.jdt.annotation.Nullable;

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

   private final @Nullable String beanName;
   private final @Nullable Class<T> beanType;
   private transient @Nullable T springBean;

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

   @SuppressWarnings({"unchecked"})
   public T get() {
      if (springBean == null) {
         springBean = (T) (beanName == null //
            ? SpringBeanLocator.get().byClass(asNonNullUnsafe(beanType))
            : SpringBeanLocator.get().byName(asNonNullUnsafe(beanName)));
      }
      return asNonNullUnsafe(springBean);
   }
}
