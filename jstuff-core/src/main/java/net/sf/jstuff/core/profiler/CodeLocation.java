/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.profiler;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

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
   public boolean equals(final @Nullable Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final CodeLocation other = (CodeLocation) obj;
      if (lineNumber != other.lineNumber //
         || !Objects.equals(clazz, other.clazz) //
         || !Objects.equals(method, other.method))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      return Objects.hash(clazz, lineNumber, method);
   }

   @Override
   public String toString() {
      final String typeFile = Strings.substringBefore(Strings.substringAfterLast(clazz, "."), '$') + ".java";
      return clazz + '.' + method + '(' + typeFile + ':' + lineNumber + ')';
   }
}
