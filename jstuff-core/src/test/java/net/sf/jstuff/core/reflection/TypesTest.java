/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import java.io.File;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.helpers.NOPLogger;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.ObjectCache;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesTest extends TestCase {

   public void testFindLibrary() {
      File library;

      // locate JDK class
      library = Types.findLibrary(String.class);
      assertNotNull(library);
      assertFalse(library.isDirectory());
      assertTrue(library.isFile());
      assertTrue(library.exists());

      // locate class in 3rd party JAR
      library = Types.findLibrary(NOPLogger.class);
      assertNotNull(library);
      assertFalse(library.isDirectory());
      assertTrue(library.isFile());
      assertTrue(library.exists());

      // locate class in exploded directory (target/classes)
      library = Types.findLibrary(ObjectCache.class);
      assertNotNull(library);
      assertTrue(library.isDirectory());
      assertFalse(library.isFile());
      assertTrue(library.exists());

      // locate anonymous inner class in exploded directory (target/classes)
      final Runnable r = () -> { /**/ };
      library = Types.findLibrary(r.getClass());
      assertNotNull(library);
      assertTrue(library.isDirectory());
      assertFalse(library.isFile());
      assertTrue(library.exists());
   }

   public void testGetVersion() {
      // from META-INF/MANIFEST.MF
      assertEquals("3.11", Types.getVersion(ObjectUtils.class));

      // from META-INF/maven/.../pom.properties
      //assertEquals("2.8", Types.getVersion(Paranamer.class));

      // from jar name
      assertEquals("4.13.1", Types.getVersion(TestCase.class));
   }

   public void testIsAssignableTo() {
      assertTrue(Types.isAssignableTo(Integer.class, Number.class));
      assertFalse(Types.isAssignableTo(Number.class, Integer.class));
      assertTrue(Types.isAssignableTo(String.class, Object.class));
   }
}
