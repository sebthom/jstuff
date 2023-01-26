/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.iterator;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeIteratorTest {

   @Test
   public void testCompositeIterator() {
      final List<String> list1 = List.of("a");
      final List<Object> list2 = List.of("b");
      final List<String> list3 = List.of("c");

      /*
       * array of iterators
       */
      final var it1 = new CompositeIterator<>(list1.iterator(), list2.iterator(), null, list3.iterator());
      assertThat(it1.next()).isEqualTo("a");
      assertThat(it1.next()).isEqualTo("b");
      assertThat(it1.next()).isEqualTo("c");

      final var it2 = Iterators.composite(list1.iterator(), list2.iterator(), null, list3.iterator());
      assertThat(it2.next()).isEqualTo("a");
      assertThat(it2.next()).isEqualTo("b");
      assertThat(it2.next()).isEqualTo("c");

      /*
       * list of iterators
       */
      final var it3 = new CompositeIterator<>(Arrays.asList(list1.iterator(), list2.iterator(), null, list3.iterator()));
      assertThat(it3.next()).isEqualTo("a");
      assertThat(it3.next()).isEqualTo("b");
      assertThat(it3.next()).isEqualTo("c");

      final var it4 = Iterators.composite(Arrays.asList(list1.iterator(), list2.iterator(), null, list3.iterator()));
      assertThat(it4.next()).isEqualTo("a");
      assertThat(it4.next()).isEqualTo("b");
      assertThat(it4.next()).isEqualTo("c");
   }
}
