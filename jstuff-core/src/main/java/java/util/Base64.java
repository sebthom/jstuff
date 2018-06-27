package java.util;

import net.sf.jstuff.core.exception.Exceptions;

public abstract class Base64 {
   public abstract static class Decoder {
      public abstract byte[] decode(String src);
   }

   public abstract static class Encoder {
      public abstract String encodeToString(byte[] src);
   }

   static {
      Exceptions.throwUnchecked(new LinkageError("This is a mock"));
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
