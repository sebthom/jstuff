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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DelegatingSecurityManagerWithThreadLocalControl extends DelegatingSecurityManager {

   private boolean isEnabledByDefault = true;

   /**
    * Determines if custom security checks implemented by this class are executed or if calls are only delegated to the underlying security manager
    */
   private final ThreadLocal<Boolean> isEnabledForThread = new ThreadLocal<Boolean>() {
      @Override
      public Boolean get() {
         final Boolean enabled = super.get();
         if (enabled == null)
            return isEnabledByDefault;
         return enabled;
      }
   };

   protected DelegatingSecurityManagerWithThreadLocalControl() {
   }

   protected DelegatingSecurityManagerWithThreadLocalControl(final SecurityManager wrapped) {
      super(wrapped);
   }

   public boolean isEnabledByDefault() {
      return isEnabledByDefault;
   }

   public boolean isEnabledForCurrentThread() {
      return isEnabledForThread.get();
   }

   public void setEnabledByDefault(final boolean enabledByDefault) {
      isEnabledByDefault = enabledByDefault;
   }

   public void setEnabledForCurrentThread(final boolean enabled) {
      isEnabledForThread.set(enabled);
   }
}
