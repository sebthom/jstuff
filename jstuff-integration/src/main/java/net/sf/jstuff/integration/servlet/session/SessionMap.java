/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet.session;

import java.util.Map;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface SessionMap extends Map<String, Object> {
   Object get(String key, Object defaultValueIfNull);

   /**
    * @return true if an underlying HTTP session exists already
    */
   boolean exists();

   /**
    * @return the session Id
    */
   Object getId();

   /**
    * invalidates the underlying HTTP session
    */
   void invalidate();
}
