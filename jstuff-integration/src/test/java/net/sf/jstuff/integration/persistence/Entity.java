/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.types.Identifiable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@jakarta.persistence.Entity
public class Entity extends Identifiable.Default<Integer> {
   private static final long serialVersionUID = 1L;

   @jakarta.persistence.Transient
   private final String _hashCodeTrackingId;

   private @Nullable Integer id;

   private @Nullable String label;

   public Entity() {
      _hashCodeTrackingId = HashCodeManager.onEntityInstantiated(this);
   }

   @Override
   public boolean equals(final @Nullable Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final Entity other = (Entity) obj;
      if (!Objects.equals(id, other.id))
         return false;
      return true;
   }

   @Override
   public @Nullable Integer getId() {
      return id;
   }

   public @Nullable String getLabel() {
      return label;
   }

   @Override
   public int hashCode() {
      return HashCodeManager.hashCodeFor(this, _hashCodeTrackingId);
   }

   @Override
   public void setId(final Integer id) {
      if (id.equals(this.id))
         return;
      if (this.id != null)
         throw new IllegalStateException("Id reassignment not allowed");
      this.id = id;

      HashCodeManager.onIdSet(this, _hashCodeTrackingId);
   }

   public Entity setLabel(final String label) {
      this.label = label;
      return this;
   }
}
