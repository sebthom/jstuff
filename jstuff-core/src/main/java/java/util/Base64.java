package java.util;

import net.sf.jstuff.core.exception.Exceptions;

public abstract class Base64 {
    static {
        Exceptions.throwUnchecked(new LinkageError("This is a mock"));
    }

    public static abstract class Decoder {
        public abstract byte[] decode(String src);
    }

    public static abstract class Encoder {
        public abstract String encodeToString(final byte[] src);
    }

    public static Decoder getDecoder() {
        throw new UnsupportedOperationException();
    }

    public static Encoder getEncoder() {
        throw new UnsupportedOperationException();
    }

    public static Decoder getUrlDecoder() {
        throw new UnsupportedOperationException();
    }

    public static Encoder getUrlEncoder() {
        throw new UnsupportedOperationException();
    }
}
