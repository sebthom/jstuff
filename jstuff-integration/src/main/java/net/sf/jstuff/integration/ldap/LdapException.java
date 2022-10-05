/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.ldap;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public LdapException(final @Nullable Throwable cause) {
      super(cause);
   }

   public LdapException(final @Nullable String message, final @Nullable Throwable cause) {
      super(message, cause);
   }
}
