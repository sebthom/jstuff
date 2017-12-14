package javax.xml.bind;

import net.sf.jstuff.core.exception.Exceptions;

public final class DatatypeConverter {
    static {
        Exceptions.throwUnchecked(new LinkageError("This is a mock"));
    }

    public static byte[] parseBase64Binary(@SuppressWarnings("unused") final String lexicalXSDBase64Binary) {
        throw new UnsupportedOperationException();
    }

    public static String printBase64Binary(@SuppressWarnings("unused") final byte[] val) {
        throw new UnsupportedOperationException();
    }
}
