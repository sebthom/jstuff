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
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.ParameterizedType;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultVisitorWithTypeArguments implements ClassVisitorWithTypeArguments {
   public boolean isVisiting(final Class<?> clazz, final ParameterizedType type) {
      return true;
   }

   public boolean isVisitingInterfaces(final Class<?> clazz, final ParameterizedType type) {
      return true;
   }

   public boolean isVisitingSuperclass(final Class<?> clazz, final ParameterizedType type) {
      return true;
   }

   public boolean visit(final Class<?> clazz, final ParameterizedType type) {
      return true;
   }
}
