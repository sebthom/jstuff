/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.functional;

import java.util.Objects;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

   void accept(A a, B b, C c);

   default TriConsumer<A, B, C> andThen(final TriConsumer<? super A, ? super B, ? super C> after) {
      Objects.requireNonNull(after);

      return (a, b, c) -> {
         accept(a, b, c);
         after.accept(a, b, c);
      };
   }
}
