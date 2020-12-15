/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SizeTest extends TestCase {

   public void testSize() {
      assertEquals(0L, Size.ofBytes(0).getBytes().longValue());
      assertEquals(0L, Size.ofKiB(0).getBytes().longValue());
      assertEquals(0L, Size.ofMiB(0).getBytes().longValue());
      assertEquals(0L, Size.ofGiB(0).getBytes().longValue());
      assertEquals(0L, Size.ofTiB(0).getBytes().longValue());

      assertEquals(1L, Size.ofBytes(1).getBytes().longValue());
      assertEquals(1L * 1024, Size.ofKiB(1).getBytes().longValue());
      assertEquals(1L * 1024 * 1024, Size.ofMiB(1).getBytes().longValue());
      assertEquals(1L * 1024 * 1024 * 1024, Size.ofGiB(1).getBytes().longValue());
      assertEquals(1L * 1024 * 1024 * 1024 * 1024, Size.ofTiB(1).getBytes().longValue());

      assertEquals(1024L, Size.ofBytes(1024).getBytes().longValue());
      assertEquals(1024L * 1024, Size.ofKiB(1024).getBytes().longValue());
      assertEquals(1024L * 1024 * 1024, Size.ofMiB(1024).getBytes().longValue());
      assertEquals(1024L * 1024 * 1024 * 1024, Size.ofGiB(1024).getBytes().longValue());
      assertEquals(1024L * 1024 * 1024 * 1024 * 1024, Size.ofTiB(1024).getBytes().longValue());
   }
}
