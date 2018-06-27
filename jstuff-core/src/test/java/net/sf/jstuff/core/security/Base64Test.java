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
package net.sf.jstuff.core.security;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Base64Test extends TestCase {

   public void testDecode() throws UnsupportedEncodingException {
      assertEquals("Hello World!", new String(Base64.decode("SGVsbG8gV29ybGQh"), "UTF-8"));
      assertEquals("Hell", new String(Base64.decode("SGVsbA=="), "UTF-8"));
      assertEquals("Hell", new String(Base64.decode("SGVsbA="), "UTF-8"));
      assertEquals("Hell", new String(Base64.decode("SGVsbA"), "UTF-8"));
      assertEquals(null, Base64.decode(null));
      assertEquals(0, Base64.decode("").length);
      assertTrue(Arrays.equals(Base64.decode("A_"), Base64.decode("A/")));
      assertTrue(Arrays.equals(Base64.decode("A-"), Base64.decode("A+")));
      assertTrue(Arrays.equals(Base64.decode("A-"), Base64.decode("A+=")));
      assertTrue(Arrays.equals(Base64.decode("A-"), Base64.decode("A+==")));
      try {
         System.out.println(new String(Base64.decode("ÖÄÜ")));
         fail();
      } catch (final Exception ex) {
         // expected
      }
   }
}
