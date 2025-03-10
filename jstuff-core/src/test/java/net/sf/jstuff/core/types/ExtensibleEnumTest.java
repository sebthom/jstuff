/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ExtensibleEnumTest {

   static final class OpenOption extends ExtensibleEnum<String> {
      private static final long serialVersionUID = 1L;

      static final OpenOption READ = new OpenOption("READ");
      static final OpenOption WRITE = new OpenOption("WRITE");

      static List<OpenOption> values() {
         return getEnumValues(OpenOption.class);
      }

      private OpenOption(final String name) {
         super(name);
      }
   }

   @Test
   void testValueOrder() {
      assertThat(OpenOption.values()).containsExactly(OpenOption.READ, OpenOption.WRITE);
   }

   @Test
   void testOrdinal() {
      assertThat(OpenOption.READ.ordinal).isEqualTo(0);
      assertThat(OpenOption.WRITE.ordinal).isEqualTo(1);
   }

   @Test
   void testSerialization() {
      assertThat(OpenOption.READ.ordinal).isLessThan(OpenOption.WRITE.ordinal);
      final var deserializedItem = (OpenOption) SerializationUtils.deserialize(SerializationUtils.serialize(OpenOption.READ));
      assertThat(deserializedItem).isSameAs(OpenOption.READ);
   }
}
