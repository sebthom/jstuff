/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
