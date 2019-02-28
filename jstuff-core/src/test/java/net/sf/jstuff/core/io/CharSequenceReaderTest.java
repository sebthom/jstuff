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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceReaderTest extends TestCase {

   @SuppressWarnings("resource")
   public void testCharSequenceReader() throws IOException {
      {
         final char[] buf = new char[5];
         final CharSequenceReader r = new CharSequenceReader("Hello World!");
         r.read(buf, 0, 5);
         assertEquals("Hello", new String(buf).intern());
      }

      {
         final char[] buf = new char[5];
         final CharSequenceReader r = new CharSequenceReader(new StringBuffer("Hello World!"));
         r.read(buf, 0, 5);
         assertEquals("Hello", new String(buf).intern());
      }

      {
         final char[] buf = new char[5];
         final CharSequenceReader r = new CharSequenceReader(new StringBuilder("Hello World!"));
         r.read(buf, 0, 5);
         assertEquals("Hello", new String(buf).intern());
      }
   }
}
