/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.tuple.Tuple2;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("restriction")
public abstract class Base64 {

    private static interface Base64Adapter {
        byte[] decode(final String encoded);

        String encode(final byte[] plain);
    }

    private static final class Base64Adapter_DatatypeConverter implements Base64Adapter {
        public byte[] decode(final String encoded) {
            return DatatypeConverter.parseBase64Binary(encoded);
        }

        public String encode(final byte[] plain) {
            return DatatypeConverter.printBase64Binary(plain);
        }
    }

    private static final class Base64Adapter_Sun implements Base64Adapter {
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

        public byte[] decode(final String encoded) {
            try {
                final byte[] bytes = encoded.getBytes("UTF-8");
                if (!isBase64(bytes))
                    throw new IllegalArgumentException("[encoded] is not a valid Base64 encoded string.");
                final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                return codec.get().get2().decodeBuffer(is);
            } catch (final IOException ex) {
                throw new IllegalArgumentException("[encoded] is not a valid Base64 encoded string.", ex);
            }
        }

        public String encode(final byte[] plain) {
            return codec.get().get1().encode(plain);
        }
    }

    private static Base64Adapter b64;

    static {
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6)) {
            try {
                b64 = new Base64Adapter_DatatypeConverter();
            } catch (final LinkageError ex) {
                b64 = new Base64Adapter_Sun();
            }
        } else {
            b64 = new Base64Adapter_Sun();
        }
    }

    public static final byte[] decode(String encoded) {
        if (encoded == null)
            return null;
        if (encoded.length() == 0)
            return ArrayUtils.EMPTY_BYTE_ARRAY;

        if (encoded.indexOf("\r") > -1) {
            encoded = Strings.remove(encoded, '\r');
        }
        if (encoded.indexOf("\n") > -1) {
            encoded = Strings.remove(encoded, '\n');
        }
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
            if (ch > 47 && ch < 58 || ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch == '+' || ch == '-' || ch == '_' || ch == '.' || ch == '/'
                    || ch == '\n') {
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
}
