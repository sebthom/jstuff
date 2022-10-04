/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Composite<Component> extends Modifiable {
   abstract class Default<Component> extends Modifiable.Default implements Composite<Component>, Serializable {
      private static final long serialVersionUID = 1L;

      protected final Collection<Component> components = createCollection();
      protected final Collection<Component> componentsUmodifiable = //
         components instanceof List //
            ? Collections.unmodifiableList((List<Component>) components)
            : components instanceof Set //
               ? Collections.unmodifiableSet((Set<Component>) components)
               : Collections.unmodifiableCollection(components);

      protected Default() {
      }

      protected Default(final boolean isModifiable, final Collection<? extends @Nullable Component> initialComponents) {
         Args.notNull("initialComponents", initialComponents);

         for (final var component : initialComponents)
            if (component != null) {
               this.components.add(component);
            }
         this.isModifiable = isModifiable;
      }

      @SafeVarargs
      protected Default(final boolean isModifiable, final @NonNullByDefault({}) Component... initialComponents) {
         Args.notNull("components", initialComponents);

         for (final var component : initialComponents)
            if (component != null) {
               this.components.add(component);
            }
         this.isModifiable = isModifiable;
      }

      protected Default(final Collection<? extends @Nullable Component> initialComponents) {
         this(true, initialComponents);
      }

      @SafeVarargs
      protected Default(final @NonNullByDefault({}) Component... initialComponents) {
         this(true, initialComponents);
      }

      protected Collection<Component> createCollection() {
         return new ArrayList<>();
      }

      @Override
      public Collection<Component> getComponents() {
         return isModifiable ? components : componentsUmodifiable;
      }
   }

   Collection<Component> getComponents();
}
