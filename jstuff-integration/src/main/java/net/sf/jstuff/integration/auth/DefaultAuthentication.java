/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.jstuff.integration.userregistry.DefaultUserDetails;
import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
class DefaultAuthentication implements Authentication {
   public static final Authentication UNBOUND = new DefaultAuthentication(new DefaultUserDetails("anonymous", "anonymous", null, null, null), null);

   private String password;
   private final Map<String, Serializable> properties = new HashMap<String, Serializable>(2);
   private UserDetails userDetails;

   DefaultAuthentication(final UserDetails userDetails, final String password) {
      this.userDetails = userDetails;
      this.password = password;
   }

   /**
    * @return the password
    */
   public String getPassword() {
      return password;
   }

   public Serializable getProperty(final String name) {
      return properties.get(name);
   }

   public UserDetails getUserDetails() {
      return userDetails;
   }

   public void invalidate() {
      userDetails = UNBOUND.getUserDetails();
      properties.clear();
      password = null;
   }

   public boolean isAuthenticated() {
      return userDetails != null && userDetails.getDistingueshedName() != null;
   }

   /**
    * @param password the password to set
    */
   public void setPassword(final String password) {
      this.password = password;
   }

   public void setProperty(final String name, final Serializable value) {
      properties.put(name, value);
   }
}
