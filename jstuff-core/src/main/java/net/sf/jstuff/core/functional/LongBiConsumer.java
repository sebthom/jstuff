/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
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
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface LongBiConsumer {

   void accept(long a, long b);

   default LongBiConsumer andThen(final LongBiConsumer after) {
      Objects.requireNonNull(after);

      return (a, b) -> {
         accept(a, b);
         after.accept(a, b);
      };
   }
}