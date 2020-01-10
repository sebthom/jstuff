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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IOUtilsTest extends TestCase {
   public void testReadBytes() throws IOException {
      final ByteArrayInputStream is = new ByteArrayInputStream("Hello World!".getBytes());
      assertEquals(5, IOUtils.readBytes(is, 5).length);

      is.reset();
      assertEquals("Hello World!", new String(IOUtils.readBytes(is, 12)));
   }

}
