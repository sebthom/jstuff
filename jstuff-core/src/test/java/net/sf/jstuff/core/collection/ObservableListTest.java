/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.ItemChange;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.Operation;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ObservableListTest {

   private ObservableList<String> list = lateNonNull();
   private List<ObservableCollection.ChangeEvent<String>> events = lateNonNull();

   @BeforeEach
   void setUp() {
      list = ObservableList.of(new ArrayList<>());
      events = new ArrayList<>();
      list.subscribe(events::add);
   }

   @Test
   void testAdd() {
      list.add("A");

      assertThat(list).containsExactly("A");
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
   void testAddAll_array() {
      final int added = list.addAll("A", "B", "C");

      assertThat(added).isEqualTo(3);
      assertThat(list).containsExactly("A", "B", "C");

      assertThat(events).hasSize(1);
      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(3);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("A", "B", "C");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(0, 1, 2);
   }

   @Test
   void testAddAll_array_withFilter() {
      final int added = list.addAll(new @NonNull String[] {"A1", "B2", "A3"}, s -> s.startsWith("A"));

      assertThat(added).isEqualTo(2);
      assertThat(list).containsExactly("A1", "A3");

      assertThat(events).hasSize(1);
      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("A1", "A3");
   }

   @Test
   void testAddAll_collection() {
      list.addAll(List.of("A", "B"));

      assertThat(list).containsExactly("A", "B");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("A", "B");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(0, 1);
   }

   @Test
   void testAddAll_iterable() {
      final int added = list.addAll((Iterable<String>) List.of("X", "Y"));

      assertThat(added).isEqualTo(2);
      assertThat(list).containsExactly("X", "Y");

      assertThat(events).hasSize(1);
      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("X", "Y");
   }

   @Test
   void testAddAll_iterable_withFilter() {
      final int added = list.addAll(List.of("AB", "C", "DE"), s -> s.length() == 2);

      assertThat(added).isEqualTo(2);
      assertThat(list).containsExactly("AB", "DE");

      assertThat(events).hasSize(1);
      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("AB", "DE");
   }

   @Test
   void testAddAll_iterator() {
      final int added = list.addAll(List.of("1", "2", "3").iterator());

      assertThat(added).isEqualTo(3);
      assertThat(list).containsExactly("1", "2", "3");

      assertThat(events).hasSize(1);
      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(3);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("1", "2", "3");
   }

   @Test
   void testAddAll_iterator_withFilter() {
      final int added = list.addAll(List.of("", "Hello", "", "World").iterator(), s -> !s.isEmpty());

      assertThat(added).isEqualTo(2);
      assertThat(list).containsExactly("Hello", "World");

      assertThat(events).hasSize(1);
      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("Hello", "World");
   }

   @Test
   void testClear() {
      list.addAll(List.of("A", "B"));
      events.clear();

      list.clear();

      assertThat(list).isEmpty();
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.CLEAR);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(0, 1);
   }

   @Test
   void testRemove() {
      list.add("A");
      events.clear();

      list.remove("A");

      assertThat(list).isEmpty();
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
      list.addAll(List.of("A", "B", "C"));
      events.clear();

      list.removeAll(List.of("B", "C"));

      assertThat(list).containsExactly("A");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE_ALL);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("B", "C");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(1, 2);
   }

   @Test
   void testRemoveIf() {
      list.addAll(List.of("A", "B", "C"));
      events.clear();

      list.removeIf(e -> !"A".equals(e));

      assertThat(list).containsExactly("A");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE_IF);
      assertThat(event.changes()).hasSize(2);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("B", "C");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(1, 2);
   }

   @Test
   void testReplaceAll() {
      list.addAll(List.of("A", "B", "C"));
      events.clear();

      list.replaceAll(s -> s.equals("B") ? "X" : s);

      assertThat(list).containsExactly("A", "X", "C");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REPLACE_ALL);
      assertThat(event.changes()).hasSize(1);

      final var change = event.changes().get(0);
      assertThat(change.type()).isEqualTo(ItemChange.Type.REPLACE);
      assertThat(change.index()).isEqualTo(1);
      assertThat(change.replacedItem()).isEqualTo("B");
      assertThat(change.item()).isEqualTo("X");
   }

   @Test
   void testRetainAll() {
      list.addAll(List.of("A", "B", "C"));
      events.clear();

      list.retainAll(List.of("A", "C"));

      assertThat(list).containsExactly("A", "C");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.RETAIN_ALL);
      assertThat(event.changes()).hasSize(1);
      final var change = event.changes().get(0);
      assertThat(change.item()).isEqualTo("B");
      assertThat(change.index()).isEqualTo(1);
   }

   @Test
   void testSet() {
      list.add("A");
      events.clear();

      list.set(0, "B");

      assertThat(list).containsExactly("B");
      assertThat(events).hasSize(1);

      final var event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.SET);
      assertThat(event.changes()).hasSize(1);
      final var change = event.changes().get(0);
      assertThat(change.type()).isEqualTo(ItemChange.Type.REPLACE);
      assertThat(change.replacedItem()).isEqualTo("A");
      assertThat(change.item()).isEqualTo("B");
      assertThat(change.index()).isEqualTo(0);
   }

   @Test
   void testSubListAddAndPropagation() {
      list.addAll(List.of("A", "B", "C", "D", "E"));
      events.clear();

      final var subList = list.subList(1, 4); // [B, C, D]
      final var subEvents = new ArrayList<ObservableCollection.ChangeEvent<String>>();
      subList.subscribe(subEvents::add);

      subList.add("X");

      assertThat(subList).containsExactly("B", "C", "D", "X");
      assertThat(list).containsExactly("A", "B", "C", "D", "X", "E");

      // Sublist event
      assertThat(subEvents).hasSize(1);
      var event = subEvents.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD);
      assertThat(event.changes()).hasSize(1);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("X");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(3);

      // Parent event
      assertThat(events).hasSize(1);
      event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.ADD);
      assertThat(event.changes()).hasSize(1);
      assertThat(event.changes().get(0).item()).isEqualTo("X");
      assertThat(event.changes().get(0).index()).isEqualTo(4);
   }

   @Test
   void testSubListClearAndPropagation() {
      list.addAll(List.of("A", "B", "C", "D", "E"));
      events.clear();

      final var subList = list.subList(1, 4); // [B, C, D]
      final var subEvents = new ArrayList<ObservableCollection.ChangeEvent<String>>();
      subList.subscribe(subEvents::add);

      subList.clear();

      assertThat(subList).isEmpty();
      assertThat(list).containsExactly("A", "E");

      // Sublist event
      assertThat(subEvents).hasSize(1);
      var event = subEvents.get(0);
      assertThat(event.operation()).isEqualTo(Operation.CLEAR);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("B", "C", "D");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(0, 1, 2);

      // Parent event
      assertThat(events).hasSize(1);
      event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.CLEAR);
      assertThat(event.changes()).extracting(ItemChange::item).containsExactly("B", "C", "D");
      assertThat(event.changes()).extracting(ItemChange::index).containsExactly(1, 2, 3);
   }

   @Test
   void testSubListRemoveAndPropagation() {
      list.addAll(List.of("A", "B", "C", "D", "E"));
      events.clear();

      final var subList = list.subList(1, 4); // [B, C, D]
      final var subEvents = new ArrayList<ObservableCollection.ChangeEvent<String>>();
      subList.subscribe(subEvents::add);

      subList.remove("C");

      assertThat(subList).containsExactly("B", "D");
      assertThat(list).containsExactly("A", "B", "D", "E");

      // Sublist event
      assertThat(subEvents).hasSize(1);
      var event = subEvents.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE);
      assertThat(event.changes()).hasSize(1);
      assertThat(event.changes().get(0).item()).isEqualTo("C");
      assertThat(event.changes().get(0).index()).isEqualTo(1);

      // Parent event
      assertThat(events).hasSize(1);
      event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.REMOVE);
      assertThat(event.changes()).hasSize(1);
      assertThat(event.changes().get(0).item()).isEqualTo("C");
      assertThat(event.changes().get(0).index()).isEqualTo(2);
   }

   @Test
   void testSubListSetAndPropagation() {
      list.addAll(List.of("A", "B", "C", "D", "E"));
      events.clear();

      final var subList = list.subList(1, 4); // [B, C, D]
      final var subEvents = new ArrayList<ObservableCollection.ChangeEvent<String>>();
      subList.subscribe(subEvents::add);

      subList.set(1, "X");

      assertThat(subList).containsExactly("B", "X", "D");
      assertThat(list).containsExactly("A", "B", "X", "D", "E");

      // Sublist event
      assertThat(subEvents).hasSize(1);
      var event = subEvents.get(0);
      assertThat(event.operation()).isEqualTo(Operation.SET);
      assertThat(event.changes()).hasSize(1);
      var change = event.changes().get(0);
      assertThat(change.replacedItem()).isEqualTo("C");
      assertThat(change.item()).isEqualTo("X");
      assertThat(change.index()).isEqualTo(1);

      // Parent event
      assertThat(events).hasSize(1);
      event = events.get(0);
      assertThat(event.operation()).isEqualTo(Operation.SET);
      assertThat(event.changes()).hasSize(1);
      change = event.changes().get(0);
      assertThat(change.replacedItem()).isEqualTo("C");
      assertThat(change.item()).isEqualTo("X");
      assertThat(change.index()).isEqualTo(2);
   }
}
