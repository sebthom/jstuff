/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeList<V> extends AbstractList<V> implements Composite<List<? extends V>>, Serializable {

   private static final long serialVersionUID = 1L;

   public static <V> CompositeList<V> of(final Collection<List<? extends V>> lists) {
      return new CompositeList<>(lists);
   }

   @SafeVarargs
   public static <V> CompositeList<V> of(final List<? extends V>... lists) {
      return new CompositeList<>(lists);
   }

   private final Collection<List<? extends V>> lists = new ArrayList<>();

   public CompositeList() {
   }

   public CompositeList(final Collection<List<? extends V>> lists) {
      this.lists.addAll(lists);
   }

   @SafeVarargs
   public CompositeList(final List<? extends V>... lists) {
      CollectionUtils.addAll(this.lists, lists);
   }

   @Override
   public void addComponent(final List<? extends V> list) {
      this.lists.add(list);
   }

   @Override
   public V get(final int index) {
      int totalSizeOfCheckedLists = 0;
      for (final List<? extends V> list : lists) {
         final int currentListIndex = index - totalSizeOfCheckedLists;
         final int currentListSize = list.size();
         if (currentListIndex >= currentListSize) {
            totalSizeOfCheckedLists += currentListSize;
            continue;
         }
         return list.get(currentListIndex);
      }
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSizeOfCheckedLists);
   }

   @Override
   public boolean hasComponent(final List<? extends V> list) {
      return lists.contains(list);
   }

   @Override
   public boolean isModifiable() {
      return true;
   }

   @Override
   public boolean removeComponent(final List<? extends V> list) {
      return lists.remove(list);
   }

   @Override
   public int size() {
      int size = 0;
      for (final List<? extends V> list : lists) {
         size += list.size();
      }
      return size;
   }
}
