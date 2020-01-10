/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security.acl;

import java.security.Permission;

/**
 * Security manager that prevents calls to {@link System#exit(int)}
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NoExitSecurityManager extends DelegatingSecurityManagerWithThreadLocalControl {

   public static class ExitNotAllowedException extends SecurityException {

      private static final long serialVersionUID = 1L;

      private final Integer status;

      public ExitNotAllowedException() {
         super("Executing java.lang.System.exit(?) is not allowed.");
         status = null;
      }

      public ExitNotAllowedException(final int status) {
         super("Executing java.lang.System.exit(" + status + ") is not allowed.");
         this.status = status;
      }

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
      if (isEnabledForThread.get())
         throw new ExitNotAllowedException(status);
      super.checkExit(status);
   }

   @Override
   public void checkPermission(final Permission perm) {
      if (isEnabledForThread.get()) {
         if (perm instanceof RuntimePermission && "exitVM".equals(perm.getName()))
            throw new ExitNotAllowedException();
      }
      super.checkPermission(perm);
   }

   @Override
   public void checkPermission(final Permission perm, final Object context) {
      if (isEnabledForThread.get()) {
         if (perm instanceof RuntimePermission && "exitVM".equals(perm.getName()))
            throw new ExitNotAllowedException();
      }
      super.checkPermission(perm, context);
   }
}
