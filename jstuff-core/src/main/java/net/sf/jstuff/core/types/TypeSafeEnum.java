/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 *
 * @deprecated use {@link ExtensibleEnum}
 */
@Deprecated(forRemoval = true, since = "8.1.0")
public abstract class TypeSafeEnum<ID> extends ExtensibleEnum<ID> {

   private static final long serialVersionUID = 1L;

   protected TypeSafeEnum(final ID id) {
      super(id);
   }
}
