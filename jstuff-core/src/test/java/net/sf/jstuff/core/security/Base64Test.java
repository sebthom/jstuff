
/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Base64Test extends TestCase {

   public void testDecode() throws UnsupportedEncodingException {

      /*Base64.decode( //
         "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM9UjXquRajbqlgDPp+lb7WegOSR/Tmm\nl56S7aTanVvR1sBVWUQZMPpspRuEHZw+FF4Zb2utZnMLdQk3ZL9nCVMCAwEAAQ==");
      */
      assertEquals("Hello World!", new String(Base64.decode("SGVsbG8gV29ybGQh"), "UTF-8"));
      assertEquals("Hello World!", new String(Base64.decode("SGVsbG8g\nV29ybGQh"), "UTF-8"));
      assertEquals("Hell", new String(Base64.decode("SGVsbA=="), "UTF-8"));
      assertEquals("Hell", new String(Base64.decode("SGVsbA="), "UTF-8"));
      assertEquals("Hell", new String(Base64.decode("SGVsbA"), "UTF-8"));
      assertEquals(null, Base64.decode((String) null));
      assertEquals(0, Base64.decode("").length);
      assertEquals(null, Base64.decode((byte[]) null));
      assertEquals(0, Base64.decode(new byte[0]).length);
      assertTrue(Arrays.equals(Base64.urldecode("A_"), Base64.decode("A/")));
      assertTrue(Arrays.equals(Base64.urldecode("A-"), Base64.decode("A+")));
      assertTrue(Arrays.equals(Base64.urldecode("A-"), Base64.decode("A+=")));
      assertTrue(Arrays.equals(Base64.urldecode("A-="), Base64.decode("A+==")));
      try {
         System.out.println(new String(Base64.decode("ÖÄÜ")));
         fail();
      } catch (final Exception ex) {
         // expected
      }
   }
}
