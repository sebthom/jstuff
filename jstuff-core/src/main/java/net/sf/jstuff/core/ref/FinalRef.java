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
public class FinalRef<T> implements Ref<T>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <T> FinalRef<T> of(final T value) {
      return new FinalRef<T>(value);
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
