/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollectionTest {

   @Test
   public void testCompositeCollection() {
      final var l1 = Arrays.asList("a", "b");
      final var l2 = Arrays.asList("c", "d");

      final var cc = CompositeCollection.of(l1, l2);
      assertThat(cc).hasSize(4);

      final var l3 = new ArrayList<>(cc);
      assertThat(l3.get(0)).isEqualTo("a");
      assertThat(l3.get(1)).isEqualTo("b");
      assertThat(l3.get(2)).isEqualTo("c");
      assertThat(l3.get(3)).isEqualTo("d");
      assertThat(l3).hasSize(4);

      try {
         cc.add("foo");
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (final UnsupportedOperationException ex) {
         // expected
      }

      try {
         cc.remove("a");
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (final UnsupportedOperationException ex) {
         // expected
      }
   }
}
