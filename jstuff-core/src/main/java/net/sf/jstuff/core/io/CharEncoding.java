/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

/**
 * Determines the character encoding of a (text) file.
 * <p>
 * Based on https://github.com/file/file/blob/master/src/encoding.c
 * <p>
 * See also http://www.iana.org/assignments/character-sets/character-sets.xhtml
 * <p>
 *
 * <pre>
 * $ file -i myfile.txt
 * myfile.txt: text/plain; charset=unknown-8bit
 * $ file myfile.txt
 * myfile.txt: Non-ISO extended-ASCII text, with CRLF line terminators
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum CharEncoding {

   BINARY("application/octet-stream", "binary", "Data"),

   ASCII(/*          */ "text/plain", "us-ascii", /*    */ "ASCII"),
   UTF_7(/*          */ "text/plain", "utf-7", /*       */ "UTF-7 Unicode"),
   UTF_8(/*          */ "text/plain", "utf-8", /*       */ "UTF-8 Unicode"),
   UTF_8_WITH_BOM(/* */ "text/plain", "utf-8", /*       */ "UTF-8 Unicode (with BOM)"),
   UTF_16_BE(/*      */ "text/plain", "utf-16be", /*    */ "Big-endian UTF-16 Unicode"),
   UTF_16_LE(/*      */ "text/plain", "utf-16le", /*    */ "Little-endian UTF-16 Unicode"),
   ISO_8859_1(/*     */ "text/plain", "usi-8859-1", /*  */ "ISO-8859"),
   UNKNOWN_8BIT(/*   */ "text/plain", "unknown-8bit", /**/ "Non-ISO extended-ASCII"),
   EBCDIC(/*         */ "text/plain", "ebcdic", /*      */ "EBCDIC"),
   EBCDIC_INTERNATIONAL("text/plain", "ebcdic", /*      */ "International EBCDIC");

   private static final byte F = 0; /* character never appears in text */
   private static final byte T = 1; /* character appears in plain ASCII text */
   private static final byte I = 2; /* character appears in ISO-8859 text */
   private static final byte X = 3; /* character appears in non-ISO extended ASCII (Mac, IBM PC) */

   private static final char[] EBCDIC_TO_ASCII = { //
      0, 1, 2, 3, 156, 9, 134, 127, 151, 141, 142, 11, 12, 13, 14, 15, //
      16, 17, 18, 19, 157, 133, 8, 135, 24, 25, 146, 143, 28, 29, 30, 31, //
      128, 129, 130, 131, 132, 10, 23, 27, 136, 137, 138, 139, 140, 5, 6, 7, //
      144, 145, 22, 147, 148, 149, 150, 4, 152, 153, 154, 155, 20, 21, 158, 26, //
      ' ', 160, 161, 162, 163, 164, 165, 166, 167, 168, 213, '.', '<', '(', '+', '|', //
      '&', 169, 170, 171, 172, 173, 174, 175, 176, 177, '!', '$', '*', ')', ';', '~', //
      '-', '/', 178, 179, 180, 181, 182, 183, 184, 185, 203, ',', '%', '_', '>', '?', //
      186, 187, 188, 189, 190, 191, 192, 193, 194, '`', ':', '#', '@', '\'', '=', '"', //
      195, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 196, 197, 198, 199, 200, 201, //
      202, 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', '^', 204, 205, 206, 207, 208, //
      209, 229, 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 210, 211, 212, '[', 214, 215, //
      216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, ']', 230, 231, //
      '{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 232, 233, 234, 235, 236, 237, //
      '}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 238, 239, 240, 241, 242, 243, //
      '\\', 159, 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 244, 245, 246, 247, 248, 249, //
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 250, 251, 252, 253, 254, 255 //
   };

   private static final byte[] TEXT_CHARS = {
      /*                  BEL BS HT LF VT FF CR    */
      F, F, F, F, F, F, F, T, T, T, T, T, T, T, F, F, /* 0x0X */
      /*                              ESC          */
      F, F, F, F, F, F, F, F, F, F, F, T, F, F, F, F, /* 0x1X */
      T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, /* 0x2X */
      T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, /* 0x3X */
      T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, /* 0x4X */
      T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, /* 0x5X */
      T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, /* 0x6X */
      T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, F, /* 0x7X */
      /*            NEL                            */
      X, X, X, X, X, T, X, X, X, X, X, X, X, X, X, X, /* 0x8X */
      X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, /* 0x9X */
      I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, /* 0xaX */
      I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, /* 0xbX */
      I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, /* 0xcX */
      I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, /* 0xdX */
      I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, /* 0xeX */
      I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I /*  0xfX */
   };

   private static byte[] from_ebcidc_to_ascii(final byte[] buf) {
      final byte[] out = new byte[buf.length];
      for (int i = 0, l = buf.length; i < l; i++) {
         out[i] = (byte) EBCDIC_TO_ASCII[unsignedByte(buf[i])];
      }
      return out;
   }

   private static boolean looks_ascii(final byte[] buf) {
      for (final byte element : buf) {
         if (TEXT_CHARS[unsignedByte(element)] != T)
            return false;
      }
      return true;
   }

   private static boolean looks_extended(final byte[] buf) {
      for (final byte element : buf) {
         final int t = TEXT_CHARS[unsignedByte(element)];
         if (t != T && t != I && t != X)
            return false;
      }
      return true;
   }

   private static boolean looks_latin1(final byte[] buf) {
      for (final byte element : buf) {
         final int t = TEXT_CHARS[unsignedByte(element)];
         if (t != T && t != I)
            return false;
      }
      return true;
   }

   /**
    * Returns:
    * <li>0: No UCS16
    * <li>1: little endian
    * <li>2: big endian
    */
   private static int looks_ucs16(final byte[] buf) {
      final int l = buf.length;
      if (l < 2)
         return 0;

      final boolean isBigEndian;
      if (unsignedByte(buf[0]) == 0xff && unsignedByte(buf[1]) == 0xfe) {
         isBigEndian = false;
      } else {
         if (unsignedByte(buf[0]) == 0xfe && unsignedByte(buf[1]) == 0xff) {
            isBigEndian = true;
         } else
            return 0;
      }

      int ubufIdx = 0;
      final char[] ubuf = new char[l];
      for (int i = 2; i + 1 < l; i += 2) {

         if (isBigEndian) {
            ubuf[ubufIdx] = (char) (unsignedByte(buf[i + 1]) + 256 * buf[i]);
         } else {
            ubuf[ubufIdx] = (char) (unsignedByte(buf[i]) + 256 * buf[i + 1]);
         }

         if (ubuf[ubufIdx] == 0xfffe || //
            ubuf[ubufIdx] < 128 && TEXT_CHARS[ubuf[ubufIdx]] != T //
         )
            return 0;

         ubufIdx++;
      }

      return isBigEndian ? 2 : 1;
   }

   private static boolean looks_utf7(final byte[] buf) {
      if (buf.length > 4 && buf[0] == '+' && buf[1] == '/' && buf[2] == 'v') {
         switch (buf[3]) {
            case '8':
            case '9':
            case '+':
            case '/':
               return true;
            default:
               return false;
         }
      }
      return false;
   }

   /**
    * Returns:
    * <li>-1: invalid UTF-8
    * <li>0: uses odd control characters, so doesn't look like text
    * <li>1: 7-bit text
    * <li>2: definitely UTF-8 text (valid high-bit set bytes)
    */
   private static int looks_utf8(final byte[] buf, final int startAt) {
      boolean containsUTF8Chars = false;
      boolean containsControlChars = false;

      for (int i = startAt, l = buf.length; i < l; i++) {
         int ubyte = unsignedByte(buf[i]);
         if ((ubyte & 0x80) == 0) { /* 0xxxxxxx is plain ASCII */
            /*
            * Even if the whole file is valid UTF-8 sequences,
            * still reject it if it uses weird control characters.
            */
            if (TEXT_CHARS[ubyte] != T) {
               containsControlChars = true;
            }
            continue;
         }

         if ((ubyte & 0x40) == 0) /* 10xxxxxx never 1st byte */
            return -1;

         /* 11xxxxxx begins UTF-8 */
         final int following;

         if ((ubyte & 0x20) == 0) { /* 110xxxxx */
            following = 1;
         } else if ((ubyte & 0x10) == 0) { /* 1110xxxx */
            following = 2;
         } else if ((ubyte & 0x08) == 0) { /* 11110xxx */
            following = 3;
         } else if ((ubyte & 0x04) == 0) { /* 111110xx */
            following = 4;
         } else if ((ubyte & 0x02) == 0) { /* 1111110x */
            following = 5;
         } else
            return -1;

         for (int n = 0; n < following; n++) {
            i++;
            if (i >= l)
               return containsControlChars ? 0 : containsUTF8Chars ? 2 : 1;

            ubyte = unsignedByte(buf[i]);
            if ((ubyte & 0x80) == 0 || (ubyte & 0x40) > 0)
               return -1;
         }
         containsUTF8Chars = true;
      }

      return containsControlChars ? 0 : containsUTF8Chars ? 2 : 1;
   }

   private static boolean looks_utf8_with_bom(final byte[] buf) {
      if (buf.length > 3 && //
         unsignedByte(buf[0]) == 0xef && //
         unsignedByte(buf[1]) == 0xbb && //
         unsignedByte(buf[2]) == 0xbf //
      )
         return looks_utf8(buf, 3) > 0;
      return false;
   }

   private static int unsignedByte(final byte value) {
      return value & 0xFF;
   }

   /**
    * E.g. "UTF-8"
    */
   public final String charset;

   /**
    * E.g. "text/plain"
    */
   public final String mimeType;

   /**
    * E.g. "text/plain; charset=UTF-8"
    */
   public final String contentType;

   /**
    * E.g. "UTF-8 Unicode (with BOM)"
    */
   public final String description;

   CharEncoding(final String mimeType, final String charset, final String description) {
      this.mimeType = mimeType;
      this.charset = charset;
      contentType = mimeType + "; charset=" + charset;
      this.description = description;
   }

   /**
    * Try to determine whether text is in some character code we can identify. It also identifies EBCDIC by converting it to ISO-8859-1.
    *
    * @return true if it could guess an encoding otherwise {@link #BINARY}
    */
   public static CharEncoding guess(final byte[] buf) {
      if (looks_ascii(buf)) {
         if (looks_utf7(buf))
            return UTF_7;
         return ASCII;
      }

      if (looks_utf8_with_bom(buf))
         return UTF_8_WITH_BOM;

      if (looks_utf8(buf, 0) > 1)
         return UTF_8;

      final int ucs_type = looks_ucs16(buf);
      if (ucs_type != 0) {
         if (ucs_type == 1)
            return UTF_16_LE;
         return UTF_16_BE;
      }

      if (looks_latin1(buf))
         return ISO_8859_1;

      if (looks_extended(buf))
         return UNKNOWN_8BIT;

      final byte[] nbuf = from_ebcidc_to_ascii(buf);

      if (looks_ascii(nbuf))
         return EBCDIC;

      if (looks_latin1(nbuf))
         return EBCDIC_INTERNATIONAL;

      return BINARY;
   }
}
