/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.ogn;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.validation.Args;

/**
 * Default object graph navigator implementation.
 *
 * Object path separator is a colon (.), e.g. owner.address.street
 *
 * The implementation currently is limited to address fields and properties. Separate items of arrays, maps or keys cannot be addressed.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObjectGraphNavigatorDefaultImpl implements ObjectGraphNavigator {
   public static final ObjectGraphNavigatorDefaultImpl INSTANCE = new ObjectGraphNavigatorDefaultImpl(false);

   private final boolean strict;

   public ObjectGraphNavigatorDefaultImpl(final boolean strict) {
      this.strict = strict;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T getValueAt(final Object root, final String path) {
      Args.notNull("root", root);
      Args.notNull("path", path);

      Object parent = null;
      Object target = root;
      for (final String chunk : path.split("\\.")) {
         parent = target;
         if (parent == null)
            return null;

         final Method getter = Methods.findAnyGetter(parent.getClass(), chunk);
         if (getter == null) {
            final Field field = Fields.findRecursive(parent.getClass(), chunk);
            if (field == null) {
               if (strict)
                  throw new IllegalArgumentException("Invalid object navigation path from root object class [" + root.getClass().getName() + "] path: " + path);
               return null;
            }
            target = Fields.read(parent, field);
         } else {
            target = Methods.invoke(parent, getter);
         }
      }
      return (T) target;
   }

   public boolean isStrict() {
      return strict;
   }

   @Override
   public ObjectGraphNavigationResult navigateTo(final Object root, final String path) {
      Args.notNull("root", root);
      Args.notNull("path", path);

      Object parent = null;
      Object target = root;
      AccessibleObject targetAccessor = null;
      for (final String chunk : path.split("\\.")) {
         parent = target;
         if (parent == null)
            return null;

         final Method getter = Methods.findAnyGetter(parent.getClass(), chunk);
         if (getter == null) {
            final Field field = Fields.findRecursive(parent.getClass(), chunk);
            if (field == null) {
               if (strict)
                  throw new IllegalArgumentException("Invalid object navigation path from root object class [" + root.getClass().getName() + "] path: " + path);
               return null;
            }
            target = Fields.read(parent, field);
            targetAccessor = field;
         } else {
            target = Methods.invoke(parent, getter);
            targetAccessor = getter;
         }
      }
      return new ObjectGraphNavigationResult(root, path, parent, targetAccessor, target);
   }
}
