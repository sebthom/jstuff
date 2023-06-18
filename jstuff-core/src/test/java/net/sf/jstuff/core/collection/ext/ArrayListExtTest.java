/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayListExtTest {

   @Test
   public void testArrayListExt() {
      final var l = new ArrayListExt<>("a", "b");

      assertThat(l.getAt(0)).isEqualTo("a");
      assertThat(l.getAt(1)).isEqualTo("b");
      assertThat(l.getAt(-1)).isEqualTo("b");
      assertThat(l.getAt(-2)).isEqualTo("a");

      try {
         l.getAt(2);
         failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
      } catch (final IndexOutOfBoundsException ex) {
         // ignore
      }

      try {
         l.getAt(-3);
         failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
      } catch (final IndexOutOfBoundsException ex) {
         // ignore
      }

      assertThat(l.getAtOrDefault(0, "X")).isEqualTo("a");
      assertThat(l.getAtOrDefault(1, "X")).isEqualTo("b");
      assertThat(l.getAtOrDefault(2, "X")).isEqualTo("X");
      assertThat(l.getAtOrDefault(-1, "X")).isEqualTo("b");
      assertThat(l.getAtOrDefault(-2, "X")).isEqualTo("a");
      assertThat(l.getAtOrDefault(-3, "X")).isEqualTo("X");
   }
}
