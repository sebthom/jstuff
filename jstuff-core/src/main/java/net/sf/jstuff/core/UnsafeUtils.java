/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.validation.Args;
import sun.misc.Unsafe; // CHECKSTYLE:IGNORE IllegalImport

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("restriction")
public abstract class UnsafeUtils {

   private static final Unsafe UNSAFE = Fields.read(null, Fields.find(Unsafe.class, "theUnsafe"));

   public static long addressOf(final Object obj) {
      Args.notNull("obj", "obj");

      switch (UNSAFE.addressSize()) {
         case 4:
            return UNSAFE.getInt(new Object[] {obj}, (long) UNSAFE.arrayBaseOffset(Object[].class));
         case 8:
            return UNSAFE.getLong(new Object[] {obj}, (long) UNSAFE.arrayBaseOffset(Object[].class));
         default:
            throw new IllegalStateException("Unsupported address size: " + UNSAFE.addressSize());
      }
   }
}
