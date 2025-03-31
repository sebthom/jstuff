/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.ItemChange;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.Operation;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ObservableSetTest {

   private ObservableSet<String> set = lateNonNull();
   private List<ObservableCollection.ChangeEvent<String>> events = lateNonNull();

   @BeforeEach
   void setUp() {
      set = ObservableSet.of(new LinkedHashSet<>()); // LinkedHashSet → predictable order
      events = new ArrayList<>();
      set.subscribe(events::add);
   }

   @Test
   void testAdd() {
      set.add("A");

      assertThat(set).containsExactly("A");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD);
      assertThat(event.changes()).hasSize(1);
      final var change = event.changes().get(0);
      assertThat(change.type()).isEqualTo(ItemChange.Type.ADD);
      assertThat(change.item()).isEqualTo("A");
      assertThat(change.index()).isEqualTo(0);
   }

   @Test
   void testAddAll() {
      set.addAll(List.of("A", "B"));

      assertThat(set).containsExactly("A", "B");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("A", "B");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(0, 1);
   }

   @Test
   void testClear() {
      set.addAll(List.of("A", "B"));
      events.clear();

      set.clear();

      assertThat(set).isEmpty();
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.CLEAR);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(0, 1);
   }

   @Test
   void testRemove() {
      set.add("A");
      events.clear();

      set.remove("A");

      assertThat(set).isEmpty();
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE);
      assertThat(event.changes()).hasSize(1);
      final var change = event.changes().get(0);
      assertThat(change.item()).isEqualTo("A");
      assertThat(change.index()).isEqualTo(0);
   }

   @Test
   void testRemoveAll() {
      set.addAll(List.of("A", "B", "C"));
      events.clear();

      set.removeAll(List.of("B", "C"));

      assertThat(set).containsExactly("A");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("B", "C");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(1, 2);
   }

   @Test
   void testRemoveIf() {
      set.addAll(List.of("A", "B", "C"));
      events.clear();

      set.removeIf(e -> !"A".equals(e));

      assertThat(set).containsExactly("A");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE_IF);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("B", "C");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(1, 2);
   }

   @Test
   void testRetainAll() {
      set.addAll(List.of("A", "B", "C"));
      events.clear();

      set.retainAll(List.of("A", "C"));

      assertThat(set).containsExactly("A", "C");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.RETAIN_ALL);
      assertThat(event.changes()).hasSize(1);
      final var change = event.changes().get(0);
      assertThat(change.item()).isEqualTo("B");
      assertThat(change.index()).isEqualTo(1);
   }
}
