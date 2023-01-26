/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UndoMarker implements Serializable {
   private static final long serialVersionUID = 1L;

   private final String id = UUID.randomUUID().toString();

   @Override
   public boolean equals(final @Nullable Object obj) {
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
