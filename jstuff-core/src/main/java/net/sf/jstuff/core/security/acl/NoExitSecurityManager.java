/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.acl;

import java.security.Permission;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Security manager that prevents calls to {@link System#exit(int)}
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NoExitSecurityManager extends DelegatingSecurityManagerWithThreadLocalControl {

   public static class ExitNotAllowedException extends SecurityException {

      private static final long serialVersionUID = 1L;

      @Nullable
      private final Integer status;

      public ExitNotAllowedException() {
         super("Executing java.lang.System.exit(?) is not allowed.");
         status = null;
      }

      public ExitNotAllowedException(final int status) {
         super("Executing java.lang.System.exit(" + status + ") is not allowed.");
         this.status = status;
      }

      @Nullable
      public Integer getStatus() {
         return status;
      }
   }

   public NoExitSecurityManager() {
   }

   public NoExitSecurityManager(final SecurityManager wrapped) {
      super(wrapped);
   }

   @Override
   public void checkExit(final int status) {
      if (isEnabledForCurrentThread())
         throw new ExitNotAllowedException(status);
      super.checkExit(status);
   }

   @Override
   public void checkPermission(final Permission perm) {
      if (isEnabledForCurrentThread() && perm instanceof RuntimePermission && "exitVM".equals(perm.getName()))
         throw new ExitNotAllowedException();
      super.checkPermission(perm);

   }

   @Override
   public void checkPermission(final Permission perm, final Object context) {
      if (isEnabledForCurrentThread() && perm instanceof RuntimePermission && "exitVM".equals(perm.getName()))
         throw new ExitNotAllowedException();
      super.checkPermission(perm, context);
   }
}
