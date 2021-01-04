/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import java.io.Serializable;

import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Authentication extends Serializable {
   String getPassword();

   Serializable getProperty(String name);

   UserDetails getUserDetails();

   void invalidate();

   boolean isAuthenticated();

   void setProperty(String name, Serializable value);
}
