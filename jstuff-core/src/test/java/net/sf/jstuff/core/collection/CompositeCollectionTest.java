/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollectionTest {

   @Test
   public void testCompositeCollection() {
      final List<String> l1 = Arrays.asList("a", "b");
      final List<String> l2 = Arrays.asList("c", "d");

      final Collection<String> cc = CompositeCollection.of(l1, l2);
      assertThat(cc).hasSize(4);

      final List<String> l3 = new ArrayList<>(cc);
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
