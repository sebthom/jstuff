/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyChangelog implements EventListener<PropertyChangeEvent>, Serializable {
   private static final long serialVersionUID = 1L;

   private final List<Object /*PropertyChangeEvent|UndoMarker*/> propertyChanges = new ArrayList<>();

   private boolean undoing;

   public void clear() {
      propertyChanges.clear();
   }

   public boolean isDirty(final JBean<?> bean) {
      return propertyChanges.stream() //
         .filter(PropertyChangeEvent.class::isInstance) //
         .map(PropertyChangeEvent.class::cast) //
         .anyMatch(p -> p.bean == bean);
   }

   public boolean isDirty(final JBean<?> bean, final PropertyDescriptor<?> prop) {
      return propertyChanges.stream() //
         .filter(PropertyChangeEvent.class::isInstance) //
         .map(PropertyChangeEvent.class::cast) //
         .anyMatch(p -> p.bean == bean && p.property == prop);
   }

   @Override
   public void onEvent(final PropertyChangeEvent event) {
      if (undoing)
         return;
      propertyChanges.add(event);
   }

   public void undo() {
      undoing = true;
      try {
         for (final var it = propertyChanges.listIterator(propertyChanges.size()); it.hasPrevious();) {
            final var change = it.previous();
            if (change instanceof PropertyChangeEvent) {
               ((PropertyChangeEvent) change).undo();
            }
            it.remove();
         }
      } finally {
         undoing = false;
      }
   }

   /**
    * @return true if the marker was found and the modifications where undone
    */
   public boolean undo(final UndoMarker until) {
      if (!propertyChanges.contains(until))
         return false;

      undoing = true;
      try {
         for (final var it = propertyChanges.listIterator(propertyChanges.size()); it.hasPrevious();) {
            final var change = it.previous();
            if (change instanceof PropertyChangeEvent) {
               ((PropertyChangeEvent) change).undo();
            }
            it.remove();

            if (change.equals(until)) {
               break;
            }
         }
      } finally {
         undoing = false;
      }
      return true;
   }

   public UndoMarker undoMarker() {
      final var um = new UndoMarker();
      propertyChanges.add(um);
      return um;
   }
}
