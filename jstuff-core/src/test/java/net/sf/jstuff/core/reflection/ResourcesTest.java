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
package net.sf.jstuff.core.reflection;

import junit.framework.TestCase;
import net.sf.jstuff.core.reflection.Resources.Resource;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ResourcesTest extends TestCase {

   public void testResources() {
      {
         boolean foundStringsClass = false;
         for (final Resource r : Resources.findResourcesByGlobPattern("**/*.class", ClassLoader.getSystemClassLoader())) {
            if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
               foundStringsClass = true;
            }
         }
         assertTrue(foundStringsClass);
      }

      {
         boolean foundStringsClass = false;
         for (final Resource r : Resources.findResourcesByGlobPattern("**/Strin*.class")) {
            if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
               foundStringsClass = true;
            }
         }
         assertTrue(foundStringsClass);
      }

      {
         boolean foundStringsClass = false;
         for (final Resource r : Resources.findResourcesByGlobPattern("Strings.class")) {
            if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
               foundStringsClass = true;
            }
         }
         assertFalse(foundStringsClass);
      }

      {
         boolean foundStringsClass = false;
         boolean foundDayProperties = false;
         for (final Resource r : Resources.findResourcesByGlobPattern("**/*.properties")) {
            if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
               foundStringsClass = true;
            }
            if (r.name.equals("net/sf/jstuff/core/date/Day.properties")) {
               foundDayProperties = true;
            }
         }
         assertFalse(foundStringsClass);
         assertTrue(foundDayProperties);
      }
   }
}
