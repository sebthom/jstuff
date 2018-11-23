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
