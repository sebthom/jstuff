/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.sf.jstuff.core.event.EventListener;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyChangeListenerDelegate implements EventListener<PropertyChangeEvent> {
   private final PropertyChangeListener listener;

   public PropertyChangeListenerDelegate(final PropertyChangeListener listener) {
      this.listener = listener;
   }

   @Override
   public void onEvent(final PropertyChangeEvent event) {
      if (event instanceof SetValueEvent) {
         final SetValueEvent ev = (SetValueEvent) event;
         listener.propertyChange(new java.beans.PropertyChangeEvent(ev.bean, ev.property.getName(), ev.oldValue, ev.newValue));
      } else if (event instanceof AddItemEvent) {
         final AddItemEvent ev = (AddItemEvent) event;
         listener.propertyChange(new IndexedPropertyChangeEvent(ev.bean, ev.property.getName(), null, ev.item, ev.index));
      } else if (event instanceof RemoveItemEvent) {
         final RemoveItemEvent ev = (RemoveItemEvent) event;
         listener.propertyChange(new IndexedPropertyChangeEvent(ev.bean, ev.property.getName(), ev.item, null, ev.index));
      }
   }
}
