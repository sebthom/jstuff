/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.io.IOException;

import junit.framework.TestCase;
import net.sf.jstuff.core.io.stream.CompositeInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeInputStreamTest extends TestCase {

   @SuppressWarnings("resource")
   public void testCompositeInputStream() throws IOException {
      final FastByteArrayInputStream bis1 = new FastByteArrayInputStream("Hello ".getBytes());
      final FastByteArrayInputStream bis2 = new FastByteArrayInputStream("World!".getBytes());

      assertEquals("Hello World!", new String(IOUtils.readBytes(new CompositeInputStream(bis1, bis2))));
      bis1.reset();
      bis2.reset();

      assertEquals("Hello", new String(IOUtils.readBytes(new CompositeInputStream(bis1, bis2), 5)));
      bis1.reset();
      bis2.reset();

      final CompositeInputStream cis = new CompositeInputStream(bis1, bis2);
      assertEquals(7, cis.skip(7));
      assertEquals("orld!", new String(IOUtils.readBytes(cis)));
   }
}
