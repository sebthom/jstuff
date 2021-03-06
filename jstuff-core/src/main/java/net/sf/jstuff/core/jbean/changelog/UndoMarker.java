/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.util.UUID;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UndoMarker extends PropertyChangeEvent {
   private static final long serialVersionUID = 1L;

   private final String id = UUID.randomUUID().toString();

   UndoMarker() {
      super(null, null);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof UndoMarker))
         return false;
      final UndoMarker other = (UndoMarker) obj;
      return id.equals(other.id);
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }

   @Override
   public void undo() {
      // do nothing
   }
}
