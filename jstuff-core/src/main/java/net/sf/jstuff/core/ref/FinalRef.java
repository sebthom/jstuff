/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.io.Serializable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FinalRef<V> implements Ref<V>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <V> FinalRef<V> of(final V value) {
      return new FinalRef<>(value);
   }

   final V value;

   public FinalRef(final V value) {
      this.value = value;
   }

   @Override
   public V get() {
      return value;
   }
}
