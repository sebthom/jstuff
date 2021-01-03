/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.persistence;

import java.util.Objects;

import net.sf.jstuff.core.types.Identifiable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@javax.persistence.Entity
public class Entity extends Identifiable.Default<Integer> {
   private static final long serialVersionUID = 1L;

   @javax.persistence.Transient
   private final String _hashCodeTrackingId;

   private Integer id;

   private String label;

   public Entity() {
      _hashCodeTrackingId = HashCodeManager.onEntityInstantiated(this);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Entity other = (Entity) obj;
      if (!Objects.equals(id, other.id)) {
         return false;
      }
      return true;
   }

   @Override
   public Integer getId() {
      return id;
   }

   public String getLabel() {
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
