/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security.acl;

import java.security.Permission;

import net.sf.jstuff.core.logging.Logger;

/**
 * Security manager that doesn't prevent anything.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NoOpSecurityManager extends SecurityManager {

   private static final Logger LOG = Logger.create();

   public boolean isInstalled() {
      return System.getSecurityManager() == this;
   }

   private SecurityManager replacedSM;

   /**
    * @return the security manager instance that got replaced by {@link #install()}
    */
   public SecurityManager getReplacedSM() {
      return replacedSM;
   }

   public synchronized void install() {
      final SecurityManager currentlyInstalledSM = System.getSecurityManager();

      // already installed?
      if (currentlyInstalledSM == this)
         return;

      // was installed before and replaced another SM?
      if (replacedSM != null) {
         LOG.warn("Security Manager [%s] has already been installed before but was not uninstalled properly.", this);
      }
      replacedSM = currentlyInstalledSM;
      System.setSecurityManager(this);
   }

   public synchronized void uninstall() {
      final SecurityManager currentlyInstalledSM = System.getSecurityManager();

      // if currently installed, restore the replaced security manager
      if (currentlyInstalledSM == this) {
         System.setSecurityManager(replacedSM);
         replacedSM = null;
         return;
      }

      if (replacedSM != null) {
         LOG.warn("Cannot restore replaced Security Manager [%s], because another security manager is active already.", currentlyInstalledSM);
      }
   }

   @Override
   public void checkPermission(final Permission perm) {
      // do nothing
   }

   @Override
   public void checkPermission(final Permission perm, final Object context) {
      // do nothing
   }
}
