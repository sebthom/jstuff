/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.profiler;

import java.util.Objects;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CodeLocation {
   public final String clazz;
   public final String method;
   public final int lineNumber;

   public CodeLocation(final String clazz, final String method, final int lineNumber) {
      this.clazz = clazz;
      this.method = method;
      this.lineNumber = lineNumber;
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final CodeLocation other = (CodeLocation) obj;
      if (!Objects.equals(clazz, other.clazz)) {
         return false;
      }
      if (lineNumber != other.lineNumber)
         return false;
      if (!Objects.equals(method, other.method)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (clazz == null ? 0 : clazz.hashCode());
      result = prime * result + lineNumber;
      result = prime * result + (method == null ? 0 : method.hashCode());
      return result;
   }

   @Override
   public String toString() {
      final String typeFile = Strings.substringBefore(Strings.substringAfterLast(clazz, "."), '$') + ".java";
      return clazz + '.' + method + '(' + typeFile + ':' + lineNumber + ')';
   }
}
