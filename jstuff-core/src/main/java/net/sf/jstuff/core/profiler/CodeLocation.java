/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.profiler;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
      if (obj == null || !(obj instanceof CodeLocation))
         return false;
      final CodeLocation other = (CodeLocation) obj;
      return clazz.equals(other.clazz) && method.equals(other.method) && lineNumber == other.lineNumber;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + clazz.hashCode();
      result = prime * result + method.hashCode();
      result = prime * result + lineNumber;
      return result;
   }

   @Override
   public String toString() {
      final String typeFile = Strings.substringBefore(Strings.substringAfterLast(clazz, "."), '$') + ".java";
      return clazz + '.' + method + '(' + typeFile + ':' + lineNumber + ')';
   }
}
