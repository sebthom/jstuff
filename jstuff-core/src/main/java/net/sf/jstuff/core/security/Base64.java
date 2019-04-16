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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * Delegates to java.util.Base64 (Java 8+), javax.xml.bind.DatatypeConverter (Java 6+) or sun.misc.BASE64Decoder (Java 5)
 * depending on the current JVM.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Base64 {

   private interface Base64Adapter {
      byte[] decode(String encoded);

      String encode(byte[] plain);
   }

   private static final class Base64Adapter_Java5 implements Base64Adapter {
      ThreadLocal<Tuple2<sun.misc.BASE64Encoder, sun.misc.BASE64Decoder>> codec = new ThreadLocal<Tuple2<sun.misc.BASE64Encoder, sun.misc.BASE64Decoder>>() {
         @Override
         protected Tuple2<sun.misc.BASE64Encoder, sun.misc.BASE64Decoder> initialValue() {
            return Tuple2.create((sun.misc.BASE64Encoder) new sun.misc.BASE64Encoder() {
               @Override
               protected void encodeLineSuffix(final OutputStream out) throws IOException {
               }
            }, new sun.misc.BASE64Decoder());
         }
      };

      @SuppressWarnings("resource")
      public byte[] decode(final String encoded) {
         try {
            final byte[] bytes = encoded.getBytes("UTF-8");
            if (!isBase64(bytes))
               throw new IllegalArgumentException("[encoded] is not a valid Base64 encoded string.");
            final FastByteArrayInputStream is = new FastByteArrayInputStream(bytes);
            return codec.get().get2().decodeBuffer(is);
         } catch (final IOException ex) {
            throw new IllegalArgumentException("[encoded] is not a valid Base64 encoded string.", ex);
         }
      }

      public String encode(final byte[] plain) {
         return codec.get().get1().encode(plain);
      }
   }

   private static final class Base64Adapter_Java6 implements Base64Adapter {
      {
         Types.initialize(javax.xml.bind.DatatypeConverter.class);
      }

      public byte[] decode(final String encoded) {
         return javax.xml.bind.DatatypeConverter.parseBase64Binary(encoded);
      }

      public String encode(final byte[] plain) {
         return javax.xml.bind.DatatypeConverter.printBase64Binary(plain);
      }
   }

   private static final class Base64Adapter_Java8 implements Base64Adapter {
      private static final java.util.Base64.Decoder DEC = java.util.Base64.getDecoder();
      private static final java.util.Base64.Encoder ENC = java.util.Base64.getEncoder();

      public byte[] decode(final String encoded) {
         return DEC.decode(encoded);
      }

      public String encode(final byte[] plain) {
         return ENC.encodeToString(plain);
      }
   }

   private static final Logger LOG = Logger.create();

   private static Base64Adapter b64;
   static {
      if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
         try {
            b64 = new Base64Adapter_Java8();
         } catch (final LinkageError ex) {
            LOG.debug(ex);
         }
      }

      if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6)) {
         try {
            b64 = new Base64Adapter_Java6();
         } catch (final LinkageError ex) {
            LOG.debug(ex);
         }
      }

      if (b64 == null) {
         b64 = new Base64Adapter_Java5();
      }
   }

   public static final byte[] decode(final String encoded) {
      if (encoded == null)
         return null;
      if (encoded.length() == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;

      if (encoded.indexOf('-') > -1 || encoded.indexOf('_') > -1 || encoded.indexOf('\n') > -1 || encoded.indexOf('\r') > -1) {
         final byte[] orig = encoded.getBytes();
         final byte[] cleaned = new byte[orig.length + 3];

         int len = 0;
         for (final byte b : orig) {
            switch (b) {
               case '-': // base64url -> base64binary encoding
                  cleaned[len] = '+';
                  len++;
                  break;
               case '_': // base64url -> base64binary encoding
                  cleaned[len] = '/';
                  len++;
                  break;
               case '\n':
                  continue;
               case '\r':
                  continue;
               default:
                  cleaned[len] = b;
                  len++;
                  break;
            }
         }
         // fix padding
         switch (len % 4) {
            case 1:
               cleaned[len] = '=';
               len++;
               //$FALL-THROUGH$
            case 2:
               cleaned[len] = '=';
               len++;
               //$FALL-THROUGH$
            case 3:
               cleaned[len] = '=';
               len++;
         }
         return b64.decode(new String(cleaned, 0, len));
      }

      // fix padding
      switch (encoded.length() % 4) {
         case 1:
            return b64.decode(encoded + "===");
         case 2:
            return b64.decode(encoded + "==");
         case 3:
            return b64.decode(encoded + "=");
         default:
            return b64.decode(encoded);
      }
   }

   public static String encode(final byte[] plain) {
      return b64.encode(plain);
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

   public static String urlencode(final byte[] plain) {
      return Strings.replaceChars(b64.encode(plain), "+/", "-_");
   }
}
