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

import static net.sf.jstuff.core.functional.Functions.*;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FunctionsTest extends TestCase {
   public void testConverts() {
      final Function<Object, Integer> t1 = objectToString()//
         .and(stringToInt())//
         .and(castTo(Number.class))//
         .and(objectToString())//
         .and(trim()) //
         .and(stringToInt());

      assertEquals(null, t1.apply(null));
      assertEquals(Integer.valueOf(1), t1.apply("1"));
   }
}
