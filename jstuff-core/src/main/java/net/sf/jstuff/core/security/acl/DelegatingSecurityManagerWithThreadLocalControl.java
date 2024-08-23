/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.acl;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Deprecated
public abstract class DelegatingSecurityManagerWithThreadLocalControl extends DelegatingSecurityManager {

   private boolean isEnabledByDefault = true;

   /**
    * Determines if custom security checks implemented by this class are executed or if calls are only delegated to the underlying security manager
    */
   private final ThreadLocal<@Nullable Boolean> isEnabledForThread = new ThreadLocal<>();

   protected DelegatingSecurityManagerWithThreadLocalControl() {
   }

   protected DelegatingSecurityManagerWithThreadLocalControl(final SecurityManager wrapped) {
      super(wrapped);
   }

   public boolean isEnabledByDefault() {
      return isEnabledByDefault;
   }

   public boolean isEnabledForCurrentThread() {
      final var enabled = isEnabledForThread.get();
      if (enabled == null)
         return isEnabledByDefault;
      return enabled;
   }

   public void setEnabledByDefault(final boolean enabledByDefault) {
      isEnabledByDefault = enabledByDefault;
   }

   public void setEnabledForCurrentThread(final boolean enabled) {
      isEnabledForThread.set(enabled);
   }
}
