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
public class SerializableMethodTest {

   @Test
   public void testSerializableMethod() throws NoSuchMethodException, SecurityException {
      final var m = SerializableMethodTest.class.getDeclaredMethod("testSerializableMethod");

      final var sm = new SerializableMethod(m);
      assertThat(sm.getMethod()).isEqualTo(m);
      assertThat(sm.getName()).isEqualTo("testSerializableMethod");
      assertThat(sm.getDeclaringClass()).isEqualTo(SerializableMethodTest.class);

      final SerializableMethod sm2 = SerializationUtils.deserialize(SerializationUtils.serialize(sm));
      assertThat(sm2.getMethod()).isEqualTo(m);
      assertThat(sm2.getName()).isEqualTo("testSerializableMethod");
      assertThat(sm2.getDeclaringClass()).isEqualTo(SerializableMethodTest.class);
   }
}
