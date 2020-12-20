/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.auth;

import java.io.Serializable;

import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Authentication extends Serializable {
   String getPassword();

   Serializable getProperty(String name);

   UserDetails getUserDetails();

   void invalidate();

   boolean isAuthenticated();

   void setProperty(String name, Serializable value);
}
