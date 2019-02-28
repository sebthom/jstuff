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

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.RandomStringUtils;

import junit.framework.TestCase;
import net.sf.jstuff.core.functional.Accept;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayUtilsTest extends TestCase {

   public void testFilter() {
      final String[] filtered = ArrayUtils.filter(new Accept<String>() {
         public boolean accept(final String obj) {
            return "foo".equals(obj) || "bar".equals(obj);
         }
      }, new String[] {"dog", "foo", "bar", "cat"});

      assertEquals(2, filtered.length);
      assertEquals("foo", filtered[0]);
      assertEquals("bar", filtered[1]);
   }

   public void testIntersect() {
      assertEquals(0, ArrayUtils.intersect((Object[]) null).length);
      assertEquals(0, ArrayUtils.intersect(ArrayUtils.EMPTY_OBJECT_ARRAY).length);
      assertEquals(0, ArrayUtils.intersect(ArrayUtils.EMPTY_OBJECT_ARRAY, null).length);

      final String[] arr1 = new String[] {"foo", "bar", "dog"};
      final String[] arr2 = new String[] {"cat", "bar", "foo"};

      assertEquals(2, ArrayUtils.intersect(arr1, arr2).length);
      assertEquals("foo", ArrayUtils.intersect(arr1, arr2)[0]);
      assertEquals("bar", ArrayUtils.intersect(arr1, arr2)[1]);
   }

   public void testToByteArray() throws UnsupportedEncodingException {
      final String str = RandomStringUtils.random(250);
      assertArrayEquals(str.getBytes("UTF-8"), ArrayUtils.toByteArray(str.toCharArray(), Charset.forName("UTF-8")));
   }
}
