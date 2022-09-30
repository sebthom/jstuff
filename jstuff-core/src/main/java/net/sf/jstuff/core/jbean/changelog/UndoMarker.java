/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UndoMarker implements Serializable {
   private static final long serialVersionUID = 1L;

   private final String id = UUID.randomUUID().toString();

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final UndoMarker other = (UndoMarker) obj;
      return id.equals(other.id);
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }
}
