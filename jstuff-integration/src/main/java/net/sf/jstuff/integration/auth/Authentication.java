/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import java.io.Serializable;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Authentication extends Serializable {
   @Nullable
   String getPassword();

   @Nullable
   Serializable getProperty(String name);

   @Nullable
   UserDetails getUserDetails();

   void invalidate();

   boolean isAuthenticated();

   void setProperty(String name, Serializable value);
}
