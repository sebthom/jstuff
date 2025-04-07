/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ogn;

import java.lang.reflect.AccessibleObject;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObjectGraphNavigationResult {
   public final Object root;
   public final String path;
   public final Object targetParent;

   /**
    * field or method
    */
   public final AccessibleObject targetAccessor;

   /**
    * accessor's value
    */
   public final @Nullable Object target;

   public ObjectGraphNavigationResult(final Object root, final String path, final Object targetParent,
         final AccessibleObject targetAccessor, final @Nullable Object target) {
      this.root = root;
      this.path = path;
      this.targetParent = targetParent;
      this.targetAccessor = targetAccessor;
      this.target = target;
   }
}
