/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.acl;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
