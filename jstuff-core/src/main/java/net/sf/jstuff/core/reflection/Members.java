/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.AccessController;
import java.security.PrivilegedAction;

import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Members {
   private static final ReflectPermission SUPPRESS_ACCESS_CHECKS_PERMISSION = new ReflectPermission("suppressAccessChecks");

   public static void assertPrivateAccessAllowed() throws ReflectionException {
      final SecurityManager mgr = System.getSecurityManager();
      if (mgr != null) {
         try {
            mgr.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
         } catch (final SecurityException ex) {
            throw new ReflectionException("Current security manager configuration does not allow access to private fields and methods.", ex);
         }
      }
   }

   public static void ensureAccessible(final AccessibleObject ao) {
      if (!ao.isAccessible()) {
         AccessController.doPrivileged((PrivilegedAction<?>) () -> {
            ao.setAccessible(true);
            return null;
         });
      }
   }

   public static boolean isAbstract(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.ABSTRACT) != 0;
   }

   public static boolean isFinal(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.FINAL) != 0;
   }

   public static boolean isPackage(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED)) == 0;
   }

   public static boolean isPrivate(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.PRIVATE) != 0;
   }

   public static boolean isPrivateAccessAllowed() {
      final SecurityManager manager = System.getSecurityManager();
      if (manager != null) {
         try {
            manager.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
         } catch (final SecurityException ex) {
            return false;
         }
      }
      return true;
   }

   public static boolean isProtected(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.PROTECTED) != 0;
   }

   public static boolean isPublic(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.PUBLIC) != 0;
   }

   public static boolean isStatic(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.STATIC) != 0;
   }

   public static boolean isTransient(final Member member) {
      Args.notNull("member", member);

      return (member.getModifiers() & Modifier.TRANSIENT) != 0;
   }
}
