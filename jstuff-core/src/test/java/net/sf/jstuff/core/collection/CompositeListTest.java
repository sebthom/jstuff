/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeListTest {

   @Test
   public void testCompositeList() {
      final List<String> l1 = Arrays.asList("a", "b");
      final List<String> l2 = Arrays.asList("c", "d");

      final List<String> cl = new CompositeList<>(l1, l2);
      assertThat(cl).hasSize(4);
      assertThat(cl.get(0)).isEqualTo("a");
      assertThat(cl.get(1)).isEqualTo("b");
      assertThat(cl.get(2)).isEqualTo("c");
      assertThat(cl.get(3)).isEqualTo("d");
      try {
         cl.get(10);
         failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
      } catch (final IndexOutOfBoundsException ex) {
         assertThat(ex.getMessage()).isEqualTo("Index: 10, Size: 4");
      }

      try {
         cl.add("foo");
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (final UnsupportedOperationException ex) {
         // expected
      }

      try {
         cl.remove(2);
         failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
      } catch (final UnsupportedOperationException ex) {
         // expected
      }
   }
}
