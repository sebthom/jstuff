/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.ldap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
