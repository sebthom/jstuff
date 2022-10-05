/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
final class AuthenticationHolder {
   private static final ThreadLocal<@Nullable Authentication> THREAD_LOCAL_AUTHENTICATION = new ThreadLocal<>();

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
   }
}
