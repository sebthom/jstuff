/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.io.Serializable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FinalRef<T> implements Ref<T>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <T> FinalRef<T> of(final T value) {
      return new FinalRef<>(value);
   }

   final T value;

   public FinalRef(final T value) {
      this.value = value;
   }

   @Override
   public T get() {
      return value;
   }
}
