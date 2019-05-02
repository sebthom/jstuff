/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Composite<Component> extends Modifiable {
   abstract class Default<Component> extends Modifiable.Default implements Composite<Component>, Serializable {
      private static final long serialVersionUID = 1L;

      protected final Collection<Component> components = createCollection();

      public Default() {
      }

      public Default(final boolean isModifiable, final Collection<? extends Component> components) {
         Args.notNull("components", components);
         this.components.addAll(components);
         this.isModifiable = isModifiable;
      }

      @SafeVarargs
      public Default(final boolean isModifiable, final Component... components) {
         Args.notNull("components", components);
         CollectionUtils.addAll(this.components, components);
         this.isModifiable = isModifiable;
      }

      public Default(final Collection<? extends Component> components) {
         this(true, components);
      }

      @SafeVarargs
      public Default(final Component... components) {
         this(true, components);
      }

      @Override
      public void addComponent(final Component component) {
         assertIsModifiable();
         components.add(component);
      }

      protected Collection<Component> createCollection() {
         return new ArrayList<>();
      }

      @Override
      public boolean hasComponent(final Component component) {
         return components.contains(component);
      }

      @Override
      public boolean removeComponent(final Component component) {
         assertIsModifiable();
         return components.remove(component);
      }
   }

   void addComponent(Component component);

   boolean hasComponent(Component component);

   boolean removeComponent(Component component);
}
