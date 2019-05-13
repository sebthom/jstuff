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

import net.sf.jstuff.core.collection.ArrayUtils;

/**
 * Delegates to java.util.Base64 (Java 8+), javax.xml.bind.DatatypeConverter (Java 6+) or sun.misc.BASE64Decoder (Java 5)
 * depending on the current JVM.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Base64 {

   public static final byte[] decode(final String encoded) {
      if (encoded == null)
         return null;
      if (encoded.length() == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      return decode(encoded.getBytes());
   }

   public static final byte[] decode(final byte[] encoded) {
      if (encoded == null)
         return null;
      if (encoded.length == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      byte[] bytes = encoded;

      if (!isBase64(bytes))
         return urldecode(bytes);

      // fix padding
      switch (bytes.length % 4) {
         case 1:
            bytes = ArrayUtils.addAll(bytes, (byte) '=', (byte) '=', (byte) '=');
            break;
         case 2:
            bytes = ArrayUtils.addAll(bytes, (byte) '=', (byte) '=');
            break;
         case 3:
            bytes = ArrayUtils.add(bytes, (byte) '=');
            break;
         default:
      }

      return java.util.Base64.getDecoder().decode(bytes);
   }

   public static String encode(final String plain) {
      if (plain == null)
         return null;
      if (plain.length() == 0)
         return "";

      return java.util.Base64.getEncoder().encodeToString(plain.getBytes());
   }

   public static String encode(final byte[] plain) {
      if (plain == null)
         return null;
      if (plain.length == 0)
         return "";

      return java.util.Base64.getEncoder().encodeToString(plain);
   }

   public static boolean isBase64(final byte[] bytes) {
      for (int i = 0; i < bytes.length; i++) {
         final byte ch = bytes[i];
         // test a-z, A-Z
         if (ch > 47 && ch < 58 || ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch == '+' || ch == '/' || ch == '\r' || ch == '\n') {
            continue;
         }
         // may end with =
         if (ch == '=') {
            if (bytes.length - i < 4) {
               continue;
            }
         }
         return false;

      }
      return true;
   }

   public static boolean isBase64Url(final byte[] bytes) {
      for (int i = 0; i < bytes.length; i++) {
         final byte ch = bytes[i];
         // test a-z, A-Z
         if (ch > 47 && ch < 58 || ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch == '-' || ch == '_' || ch == '\r' || ch == '\n') {
            continue;
         }
         // may end with =
         if (ch == '=') {
            if (bytes.length - i < 4) {
               continue;
            }
         }
         return false;

      }
      return true;
   }

   public static byte[] urldecode(final String encoded) {
      if (encoded == null)
         return null;
      if (encoded.length() == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      return java.util.Base64.getUrlDecoder().decode(encoded);
   }

   public static byte[] urldecode(final byte[] encoded) {
      if (encoded == null)
         return null;
      if (encoded.length == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      return java.util.Base64.getUrlDecoder().decode(encoded);
   }

   public static String urlencode(final String plain) {
      if (plain == null)
         return null;
      if (plain.length() == 0)
         return "";

      return java.util.Base64.getUrlEncoder().encodeToString(plain.getBytes());
   }

   public static String urlencode(final byte[] plain) {
      if (plain == null)
         return null;
      if (plain.length == 0)
         return "";

      return java.util.Base64.getUrlEncoder().encodeToString(plain);
   }
}
