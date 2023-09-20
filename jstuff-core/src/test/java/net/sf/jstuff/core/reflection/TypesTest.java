/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.slf4j.helpers.NOPLogger;

import net.sf.jstuff.core.collection.ObjectCache;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesTest {

   @Test
   @SuppressWarnings("null")
   public void testFindLibrary() {
      File library;

      // locate JDK class
      library = Types.findLibrary(String.class);
      assertThat(library) //
         .exists() //
         .isFile();

      // locate class in 3rd party JAR
      library = Types.findLibrary(NOPLogger.class);
      assertThat(library) //
         .exists() //
         .isFile();

      // locate class in exploded directory (target/classes)
      library = Types.findLibrary(ObjectCache.class);
      assertThat(library) //
         .exists() //
         .isDirectory();

      // locate anonymous inner class in exploded directory (target/classes)
      final Runnable r = () -> { /**/ };
      library = Types.findLibrary(r.getClass());
      assertThat(library) //
         .exists() //
         .isDirectory();
   }

   @Test
   public void testGetVersion() {
      // from META-INF/MANIFEST.MF
      assertThat(Types.getVersion(ObjectUtils.class)).isEqualTo("3.13.0");

      // from META-INF/maven/.../pom.properties
      assertThat(Types.getVersion(JXPathContext.class)).isEqualTo("1.3");

      // from jar name
      // assertThat(Types.getVersion(TestCase.class)).isEqualTo("4.13.2");
   }

   @Test
   public void testIsAssignableTo() {
      assertThat(Types.isAssignableTo(Integer.class, Number.class)).isTrue();
      assertThat(Types.isAssignableTo(Number.class, Integer.class)).isFalse();
      assertThat(Types.isAssignableTo(String.class, Object.class)).isTrue();
   }
}
