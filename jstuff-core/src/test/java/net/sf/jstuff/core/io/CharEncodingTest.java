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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharEncodingTest extends TestCase {

   private void assertEncoding(final CharEncoding expected, final byte[] data) {
      assertEquals(expected, CharEncoding.guess(data));
   }

   public void testAscii() {
      assertEncoding(CharEncoding.ASCII, new byte[] {'a', 'b', 'c', 'A', 'B', 'C', '1', '2', '3'});
   }

   @Test
   public void testExtendedAscii() {
      assertEncoding(CharEncoding.UNKNOWN_8BIT, new byte[] {'a', 'b', 'c', 'A', 'B', 'C', '1', '2', '3', (byte) 0x96});
   }

   public void testISO() {
      assertEncoding(CharEncoding.ISO_8859_1, new byte[] {'a', 'b', 'c', 'A', 'B', 'C', '1', '2', '3', (byte) 0xF7});
   }

   @Test
   public void testUTF_16BE() {
      assertEncoding(CharEncoding.UTF_16_BE, new byte[] {(byte) 0xFE, (byte) 0xFF, (byte) 0x00, (byte) 0xD6, (byte) 0x00, (byte) 0x41});
   }

   public void testUTF_16LE() {
      assertEncoding(CharEncoding.UTF_16_LE, new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0xD6, (byte) 0x00, (byte) 0x41, (byte) 0x00});
   }

   public void testUTF7() {
      assertEncoding(CharEncoding.UTF_7, new byte[] {'+', '/', 'v', '8', 'B', 'C', '1', '2', '3'});
      assertEncoding(CharEncoding.UTF_7, new byte[] {'+', '/', 'v', '9', 'B', 'C', '1', '2', '3'});
      assertEncoding(CharEncoding.UTF_7, new byte[] {'+', '/', 'v', '+', 'B', 'C', '1', '2', '3'});
      assertEncoding(CharEncoding.UTF_7, new byte[] {'+', '/', 'v', '/', 'B', 'C', '1', '2', '3'});
   }

   public void testUTF8() {
      assertEncoding(CharEncoding.UTF_8, new byte[] {'a', 'b', 'c', 'A', 'B', 'C', '1', '2', '3', (byte) 0xC3, (byte) 0xB6});
   }

   public void testUTF8WithBOM() {
      assertEncoding(CharEncoding.UTF_8_WITH_BOM, new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'a', 'b', 'c', 'A', 'B', 'C', '1', '2', '3'});
      assertEncoding(CharEncoding.UTF_8_WITH_BOM, new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'a', 'b', 'c', 'A', 'B', 'C', '1', '2', '3', (byte) 0xC3,
         (byte) 0xB6});
   }
}
