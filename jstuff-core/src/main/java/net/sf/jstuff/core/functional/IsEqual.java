/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
