/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import java.nio.charset.StandardCharsets;

import net.sf.jstuff.core.collection.ArrayUtils;

/**
 * Delegates to java.util.Base64 (Java 8+), javax.xml.bind.DatatypeConverter (Java 6+) or sun.misc.BASE64Decoder (Java 5)
 * depending on the current JVM.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Base64 {

   public static final byte[] decode(final byte[] encoded) {
      if (encoded == null)
         return null;
      if (encoded.length == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      final byte[] bytes = encoded;

      if (isBase64Url(bytes))
         return urldecode(bytes);

      return java.util.Base64.getDecoder().decode(sanitizeBytes(bytes));
   }

   public static final byte[] decode(final String encoded) {
      if (encoded == null)
         return null;
      if (encoded.length() == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      return decode(encoded.getBytes(StandardCharsets.UTF_8));
   }

   public static String encode(final byte[] plain) {
      if (plain == null)
         return null;
      if (plain.length == 0)
         return "";

      return java.util.Base64.getEncoder().encodeToString(plain);
   }

   public static String encode(final String plain) {
      if (plain == null)
         return null;
      if (plain.length() == 0)
         return "";

      return java.util.Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
   }

   public static boolean isBase64(final byte[] bytes) {
      for (final byte ch : bytes) {
         // test a-z, A-Z, +, /, \n, \r, =
         if (ch > 47 && ch < 58 || ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch == '+' || ch == '/' || ch == '\r' || ch == '\n' || ch == '=') {
            continue;
         }
         return false;

      }
      return true;
   }

   public static boolean isBase64Url(final byte[] bytes) {
      for (final byte ch : bytes) {
         // test a-z, A-Z, -, _, \n, \r, =
         if (ch > 47 && ch < 58 || ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch == '-' || ch == '_' || ch == '\r' || ch == '\n' || ch == '=') {
            continue;
         }
         return false;

      }
      return true;
   }

   private static byte[] sanitizeBytes(final byte[] bytes) {
      /*
       * count new line chars
       */
      int newLineChars = 0;
      for (final byte ch : bytes) {
         if (ch == '\r' || ch == '\n') {
            newLineChars++;
         }
      }

      final int bytesWithoutNewLines = bytes.length - newLineChars;
      final byte[] bytesSanitized;
      /*
       * fix padding
       */
      switch (bytesWithoutNewLines % 4) {
         case 1:
            bytesSanitized = new byte[bytesWithoutNewLines + 3];
            bytesSanitized[bytesWithoutNewLines] = '=';
            bytesSanitized[bytesWithoutNewLines + 1] = '=';
            bytesSanitized[bytesWithoutNewLines + 2] = '=';
            break;
         case 2:
            bytesSanitized = new byte[bytesWithoutNewLines + 2];
            bytesSanitized[bytesWithoutNewLines] = '=';
            bytesSanitized[bytesWithoutNewLines + 1] = '=';
            break;
         case 3:
            bytesSanitized = new byte[bytesWithoutNewLines + 1];
            bytesSanitized[bytesWithoutNewLines] = '=';
            break;
         default:
            if (newLineChars == 0)
               return bytes;
            bytesSanitized = new byte[bytesWithoutNewLines];
      }

      /*
       * remove new line chars
       */
      int i = 0;
      for (final byte ch : bytes) {
         if (ch != '\r' && ch != '\n') {
            bytesSanitized[i] = ch;
            i++;
         }
      }
      return bytesSanitized;
   }

   public static byte[] urldecode(final byte[] encoded) {
      if (encoded == null)
         return null;
      if (encoded.length == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      return java.util.Base64.getUrlDecoder().decode(sanitizeBytes(encoded));
   }

   public static byte[] urldecode(final String encoded) {
      if (encoded == null)
         return null;
      if (encoded.length() == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      return urldecode(encoded.getBytes(StandardCharsets.UTF_8));
   }

   public static String urlencode(final byte[] plain) {
      if (plain == null)
         return null;
      if (plain.length == 0)
         return "";

      return java.util.Base64.getUrlEncoder().encodeToString(plain);
   }

   public static String urlencode(final String plain) {
      if (plain == null)
         return null;
      if (plain.length() == 0)
         return "";

      return java.util.Base64.getUrlEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
   }
}
