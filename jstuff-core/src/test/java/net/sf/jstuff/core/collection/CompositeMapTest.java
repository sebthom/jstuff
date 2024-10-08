/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CompositeMapTest {

   @Test
   void testCompositeList() {
      final Map<String, String> m1 = Maps.toMap("a=1;b=2;c=3", ";", "=");
      final Map<String, String> m2 = Maps.toMap("c=4;d=5;e=6", ";", "=");

      final CompositeMap<String, String> cm = CompositeMap.of(m1, m2);
      assertThat(cm).hasSize(5);
      assertThat(cm.keySet()).hasSize(5);
      assertThat(cm.values()).hasSize(5);
      assertThat(cm).containsEntry("a", "1");
      assertThat(cm).containsEntry("c", "3");

      m1.put("a", "X");
      m1.put("d", "Y");
      m2.put("f", "Z");
      assertThat(cm).hasSize(6);
      assertThat(cm.keySet()).hasSize(6);
      assertThat(cm.values()).hasSize(6);
      try {
         cm.put("f", "f");
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (final UnsupportedOperationException ex) {
         // expected
      }

      try {
         cm.remove("a");
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (final UnsupportedOperationException ex) {
         // expected
      }
   }
}
