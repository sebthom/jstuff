/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.collection.Maps;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class AnnotationsTest {

   @Test
   void testCreate() {
      final Map<String, Object> m1 = Maps.newHashMap("value", "hello!");
      final Map<String, Object> m2 = Maps.newHashMap("value", "hi!");

      final Disabled a1a = Annotations.create(Disabled.class, m1);
      final Disabled a1b = Annotations.create(Disabled.class, m1);
      final Disabled a2 = Annotations.create(Disabled.class, m2);

      assertThat(a1b) //
         .isEqualTo(a1a) //
         .isNotEqualTo(a2);
      assertThat(a1a).hasToString("@org.junit.jupiter.api.Disabled(value=hello!)");
      assertThat(a2).hasToString("@org.junit.jupiter.api.Disabled(value=hi!)");
   }
}
