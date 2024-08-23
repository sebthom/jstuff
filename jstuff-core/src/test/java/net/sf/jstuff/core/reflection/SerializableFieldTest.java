/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SerializableFieldTest {

   public String testField = "";

   @Test
   public void testSerializableField() throws SecurityException, NoSuchFieldException {
      final var f = SerializableFieldTest.class.getDeclaredField("testField");

      final var sf = new SerializableField(f);
      assertThat(sf.getField()).isEqualTo(f);
      assertThat(sf.getName()).isEqualTo("testField");
      assertThat(sf.getDeclaringClass()).isEqualTo(SerializableFieldTest.class);

      final SerializableField sf2 = SerializationUtils.deserialize(SerializationUtils.serialize(sf));
      assertThat(sf2.getField()).isEqualTo(f);
      assertThat(sf2.getName()).isEqualTo("testField");
      assertThat(sf2.getDeclaringClass()).isEqualTo(SerializableFieldTest.class);
   }
}
