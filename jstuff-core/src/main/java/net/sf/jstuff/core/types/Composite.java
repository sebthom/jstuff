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
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Composite<Component> {
   abstract class Default<Component> implements Composite<Component>, Serializable {
      private static final long serialVersionUID = 1L;

      protected final Collection<Component> components = createCollection();

      public Default() {
         super();
      }

      public Default(final Collection<? extends Component> components) {
         Args.notNull("components", components);
         this.components.addAll(components);
      }

      public Default(final Component... components) {
         Args.notNull("components", components);
         CollectionUtils.addAll(this.components, components);
      }

      public void addComponent(final Component component) {
         Assert.isTrue(isCompositeModifiable(), "Adding components to this composite is not allowed!");
         components.add(component);
      }

      protected Collection<Component> createCollection() {
         return new ArrayList<Component>();
      }

      public boolean hasComponent(final Component component) {
         return components.contains(component);
      }

      public boolean isCompositeModifiable() {
         return true;
      }

      public boolean removeComponent(final Component component) {
         Assert.isTrue(isCompositeModifiable(), "Removing components from this composite is not allowed!");
         return components.remove(component);
      }
   }

   void addComponent(Component component);

   boolean hasComponent(Component component);

   boolean isCompositeModifiable();

   boolean removeComponent(Component component);
}
