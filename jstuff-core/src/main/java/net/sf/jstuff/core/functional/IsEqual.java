/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.functional;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface IsEqual<T> {
   IsEqual<Object> DEFAULT = new IsEqual<Object>() {
      public boolean isEqual(final Object obj1, final Object obj2) {
         return ObjectUtils.equals(obj1, obj2);
      }
   };

   boolean isEqual(T obj1, T obj2);
}
