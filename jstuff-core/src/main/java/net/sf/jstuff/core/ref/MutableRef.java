/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.ref;

import java.io.Serializable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MutableRef<T> implements Ref<T>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <T> MutableRef<T> of(final T value) {
      return new MutableRef<T>(value);
   }

   private T value;

   public MutableRef() {
      super();
   }

   public MutableRef(final T value) {
      this.value = value;
   }

   @Override
   public T get() {
      return value;
   }

   public void set(final T value) {
      this.value = value;
   }
}
