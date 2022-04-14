/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import java.lang.module.ModuleDescriptor;
import java.lang.reflect.Field;

import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.validation.Args;
import sun.misc.Unsafe; // CHECKSTYLE:IGNORE IllegalImport

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class UnsafeUtils {

   public static final Unsafe UNSAFE;
   private static final long MODULE_DESCR_OPEN_FIELD_OFFSET;
   static {
      try {
         // not using net.sf.jstuff.core.reflection.Fields to avoid circular dependency in class initialization
         final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
         theUnsafe.setAccessible(true);
         UNSAFE = (Unsafe) theUnsafe.get(null);
         MODULE_DESCR_OPEN_FIELD_OFFSET = UNSAFE.objectFieldOffset(ModuleDescriptor.class.getDeclaredField("open"));
      } catch (final ReflectiveOperationException ex) {
         throw new ReflectionException(ex);
      }
   }

   public static long addressOf(final Object obj) {
      Args.notNull("obj", obj);

      switch (UNSAFE.addressSize()) {
         case 4:
            return UNSAFE.getInt(new Object[] {obj}, UNSAFE.arrayBaseOffset(Object[].class));
         case 8:
            return UNSAFE.getLong(new Object[] {obj}, UNSAFE.arrayBaseOffset(Object[].class));
         default:
            throw new IllegalStateException("Unsupported address size: " + UNSAFE.addressSize());
      }
   }

   public static void openModule(final Module module) {
      if (module == null)
         return;

      final ModuleDescriptor descr = module.getDescriptor();
      if (descr == null)
         return;

      if (!descr.isOpen()) {
         UNSAFE.putBoolean(descr, MODULE_DESCR_OPEN_FIELD_OFFSET, true);
      }
   }
}
