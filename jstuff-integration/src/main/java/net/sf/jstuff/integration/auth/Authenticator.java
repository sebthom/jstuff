/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Authenticator {
   /**
    * @return true if authenticated, false if not
    */
   boolean authenticate(String logonName, String password);
}
