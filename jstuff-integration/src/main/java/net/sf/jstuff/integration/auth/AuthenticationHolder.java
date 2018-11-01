/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
final class AuthenticationHolder {
   private static final ThreadLocal<Authentication> THREAD_LOCAL_AUTHENTICATION = new ThreadLocal<Authentication>();

   public static Authentication getAuthentication() {
      final Authentication auth = THREAD_LOCAL_AUTHENTICATION.get();
      if (auth == null)
         return DefaultAuthentication.UNBOUND;
      return auth;
   }

   public static void setAuthentication(final Authentication authentication) {
      THREAD_LOCAL_AUTHENTICATION.set(authentication);
   }

   private AuthenticationHolder() {
      super();
   }
}
