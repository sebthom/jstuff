/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.ldap;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public LdapException(final Throwable cause) {
      super(cause);
   }

   public LdapException(final String message, final Throwable cause) {
      super(message, cause);
   }
}
