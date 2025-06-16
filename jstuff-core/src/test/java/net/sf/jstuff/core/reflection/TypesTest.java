/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.TMException;
import org.eclipse.urischeme.IUriSchemeHandler;
import org.jdom.CDATA;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import net.sf.jstuff.core.collection.ObjectCache;
import net.sf.jstuff.core.reflection.exception.ReflectionException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class TypesTest {

   @Test
   @SuppressWarnings("null")
   void testFindLibrary() {
      File library = Types.findLibrary(String.class);

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
   void testGetMinimumJavaVersion() {
      assertThat(Types.getMinimumJavaVersion(String.class)).isEqualTo(Runtime.version().feature());
      assertThat(Types.getMinimumJavaVersion(Types.class)).isEqualTo(17);
      assertThat(Types.getMinimumJavaVersion(CDATA.class)).isEqualTo(1);
      assertThat(Types.getMinimumJavaVersion(TMException.class)).isEqualTo(8);
      assertThat(Types.getMinimumJavaVersion(IUriSchemeHandler.class)).isEqualTo(8);
   }

   @Test
   void testGetVersion() {
      // from META-INF/MANIFEST.MF Implementation-Version
      assertThat(Types.getVersion(CDATA.class)).isEqualTo("1.0 beta7");

      // from META-INF/maven/.../pom.properties
      assertThat(Types.getVersion(TMException.class)).isEqualTo("0.2.0-SNAPSHOT");

      // from jar name
      assertThat(Types.getVersion(IUriSchemeHandler.class)).isEqualTo("1.0.0");
   }

   @Test
   void testIsAssignableTo() {
      assertThat(Types.isAssignableTo(Integer.class, Number.class)).isTrue();
      assertThat(Types.isAssignableTo(Number.class, Integer.class)).isFalse();
      assertThat(Types.isAssignableTo(String.class, Object.class)).isTrue();
   }

   @Test
   void testResolveUnderlyingClass() {
      /** Helper to capture generic type information */
      abstract class TypeReference<T> {
         final Type type = asNonNull((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      }

      // 1. Test with a concrete class.
      assertThat(Types.resolveUnderlyingClass(String.class)) //
         .as("Concrete class should resolve to itself") //
         .isEqualTo(String.class);

      // 2. Test with a parameterized type, e.g. List<String> => should resolve to List
      assertThat(Types.resolveUnderlyingClass(new TypeReference<List<String>>() {}.type)) //
         .as("Parameterized type should resolve to its raw type") //
         .isEqualTo(List.class);

      // 3. Test with a generic array type, e.g. List<String>[] => should resolve to List[]
      assertThat(Types.resolveUnderlyingClass(new TypeReference<List<String>[]>() {}.type)) //
         .as("Generic array type should resolve to an array class of the raw type") //
         .isEqualTo(List[].class);

      // 4. Test with a type variable.
      class GenericHolder<T extends Number> {
      }

      assertThat(Types.resolveUnderlyingClass(GenericHolder.class.getTypeParameters()[0])) //
         .as("Type variable with bound Number should resolve to Number.class") //
         .isEqualTo(Number.class);

      class GenericHolder2<T extends List<String>> {
      }

      assertThat(Types.resolveUnderlyingClass(GenericHolder2.class.getTypeParameters()[0])).as(
         "The underlying raw type for a parameterized bound should be List.class").isEqualTo(List.class);
   }

   @Test
   void testWriteBooleanProperty() {
      class Entity {
         private @Nullable Boolean isArchived;
         private boolean deleted;
         private boolean hasParent;

         @Nullable
         Boolean isArchived() {
            return isArchived;
         }

         boolean isDeleted() {
            return deleted;
         }

         boolean hasParent() {
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
