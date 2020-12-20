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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.reflection.Resources.Resource;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("deprecation")
public class ResourcesTest {

   @Test
   public void testFindClassInDir() throws IOException {
      boolean foundClass = false;

      for (final Resource r : Resources.findResourcesByGlobPattern("Strings.class")) {
         if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
            foundClass = true;
         }
      }
      assertThat(foundClass).isFalse();

      for (final Resource r : Resources.findResourcesByGlobPattern("**/Strin*.class")) {
         if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
            foundClass = true;
            try (InputStream is = r.url.openStream()) {
               is.read();
            }
         }
      }
      assertThat(foundClass).isTrue();
   }

   @Test
   public void testFindClassInJar() throws IOException {
      boolean foundClass = false;
      for (final Resource r : Resources.findResourcesByGlobPattern("**/*.class", ClassLoader.getSystemClassLoader())) {
         if (r.name.equals("org/apache/commons/lang3/SystemUtils.class")) {
            foundClass = true;
            try (InputStream is = r.url.openStream()) {
               is.read();
            }
         }
      }
      assertThat(foundClass).isTrue();
   }

   @Test
   public void testFindProperties() throws IOException {
      boolean foundClass = false;
      boolean foundDayProperties = false;
      for (final Resource r : Resources.findResourcesByGlobPattern("**/*.properties")) {
         if (r.name.equals("net/sf/jstuff/core/Strings.class")) {
            foundClass = true;
            try (InputStream is = r.url.openStream()) {
               is.read();
            }
         }
         if (r.name.equals("net/sf/jstuff/core/date/Day.properties")) {
            foundDayProperties = true;
            try (InputStream is = r.url.openStream()) {
               System.out.println(IOUtils.toString(is));
            }
         }
      }
      assertThat(foundClass).isFalse();
      assertThat(foundDayProperties).isTrue();
   }
}
