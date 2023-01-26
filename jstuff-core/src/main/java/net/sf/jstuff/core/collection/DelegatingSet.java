/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Set;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingSet<V> extends DelegatingCollection<V> implements Set<V> {
   private static final long serialVersionUID = 1L;

   public DelegatingSet(final Set<V> delegate) {
      super(delegate);
   }
}
