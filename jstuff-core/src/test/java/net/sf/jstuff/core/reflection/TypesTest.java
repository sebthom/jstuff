/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.TMException;
import org.eclipse.urischeme.IUriSchemeHandler;
import org.jdom.CDATA;
import org.junit.Test;
import org.slf4j.helpers.NOPLogger;

import net.sf.jstuff.core.collection.ObjectCache;
import net.sf.jstuff.core.reflection.exception.ReflectionException;

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
      // from META-INF/MANIFEST.MF Implementation-Version
      assertThat(Types.getVersion(CDATA.class)).isEqualTo("1.0 beta7");

      // from META-INF/maven/.../pom.properties
      assertThat(Types.getVersion(TMException.class)).isEqualTo("0.2.0-SNAPSHOT");

      // from jar name
      assertThat(Types.getVersion(IUriSchemeHandler.class)).isEqualTo("1.0.0");
   }

   @Test
   public void testIsAssignableTo() {
      assertThat(Types.isAssignableTo(Integer.class, Number.class)).isTrue();
      assertThat(Types.isAssignableTo(Number.class, Integer.class)).isFalse();
      assertThat(Types.isAssignableTo(String.class, Object.class)).isTrue();
   }

   @Test
   public void testWriteBooleanProperty() {
      class Entity {
         private @Nullable Boolean isArchived;
         private boolean deleted;
         private boolean hasParent;

         public @Nullable Boolean isArchived() {
            return isArchived;
         }

         public boolean isDeleted() {
            return deleted;
         }

         public boolean hasParent() {
            return hasParent;
         }
      }

      final var e = new Entity();
      Types.writeProperty(e, "archived", true);
      assertThat(e.isArchived()).isTrue();
      Types.writeProperty(e, "deleted", true);
      assertThat(e.isDeleted()).isTrue();
      Types.writeProperty(e, "parent", true);
      assertThat(e.hasParent()).isTrue();

      e.isArchived = false;
      e.deleted = false;
      e.hasParent = false;
      Types.writeProperty(e, "isArchived", true);
      assertThat(e.isArchived()).isTrue();
      Types.writeProperty(e, "isDeleted", true);
      assertThat(e.isDeleted()).isTrue();
      assertThatThrownBy( //
         () -> Types.writeProperty(e, "isParent", true) //
      ).isInstanceOf(ReflectionException.class);
      assertThat(e.hasParent()).isFalse();
      Types.writeProperty(e, "hasParent", true);
      assertThat(e.hasParent()).isTrue();
   }
}
