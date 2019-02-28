/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingSet<V> extends DelegatingCollection<V> implements Set<V> {
   private static final long serialVersionUID = 1L;

   public DelegatingSet(final Set<V> delegate) {
      super(delegate);
   }
}
