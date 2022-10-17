/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjIntConsumer;
import java.util.zip.ZipFile;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class IOUtils extends org.apache.commons.io.IOUtils {

   private static final Logger LOG = Logger.create();

   public static void closeQuietly(final @Nullable Closeable closeable) {
      try {
         if (closeable != null) {
            closeable.close();
         }
      } catch (final IOException ioe) {
         // ignore
      }
   }

   public static void closeQuietly(final @Nullable InputStream input) {
      closeQuietly((Closeable) input);
   }

   public static void closeQuietly(final @Nullable OutputStream output) {
      closeQuietly((Closeable) output);
   }

   public static void closeQuietly(final @Nullable Reader reader) {
      closeQuietly((Closeable) reader);
   }

   public static void closeQuietly(final @Nullable Selector selector) {
      closeQuietly((Closeable) selector);
   }

   public static void closeQuietly(final @Nullable ServerSocket socket) {
      closeQuietly((Closeable) socket);
   }

   public static void closeQuietly(final @Nullable Socket socket) {
      closeQuietly((Closeable) socket);
   }

   public static void closeQuietly(final @Nullable Writer writer) {
      closeQuietly((Closeable) writer);
   }

   public static void closeQuietly(final @Nullable ZipFile file) {
      if (file != null) {
         try {
            file.close();
         } catch (final IOException ex) {
            // ignore
         }
      }
   }

   /**
    * Copies bytes from an {@code InputStream} to an {@code OutputStream}.
    * <p>
    * This method uses the provided buffer, so there is no need to use a {@code BufferedInputStream}.
    * </p>
    *
    * @param in the {@code InputStream} to read.
    * @param out the {@code OutputStream} to write.
    * @param buffer the buffer to use for the copy
    * @param onBeforeWrite a callback that is called before each write operation
    * @return the number of bytes copied.
    */
   @SuppressWarnings("resource")
   public static long copy(final InputStream in, final OutputStream out, final byte[] buffer, final ObjIntConsumer<byte[]> onBeforeWrite)
      throws IOException {
      Args.notNull("in", in);
      Args.notNull("out", out);

      long count = 0;
      int n;
      while (EOF != (n = in.read(buffer))) {
         onBeforeWrite.accept(buffer, n);
         out.write(buffer, 0, n);
         count += n;
      }
      return count;
   }

   /**
    * @return number of bytes copied
    */
   public static long copyAndClose(final InputStream is, final OutputStream os) throws IOException {
      try {
         return copyLarge(is, os);
      } finally {
         closeQuietly(is);
         closeQuietly(os);
      }
   }

   @SuppressWarnings("resource")
   public static byte[] readBytes(final InputStream is) throws IOException {
      final var os = new FastByteArrayOutputStream();
      copyLarge(is, os);
      return os.toByteArray();
   }

   /**
    * Reads <code>len</code> bytes of data from the input stream into
    * an array of bytes. This method blocks until the given number of bytes could be read.
    *
    * @param b the buffer into which the data is read.
    * @param off the start offset in array <code>b</code> at which the data is written.
    * @param len the exact number of bytes to read.
    */
   public static void readBytes(final InputStream is, final byte[] b, final int off, final int len) throws IOException {
      Args.notNull("b", b);
      Args.inRange("off", off, 0, b.length - 1);
      Args.inRange("len", len, 0, b.length - off);

      int currentOffset = off;
      int bytesMissing = len;
      while (bytesMissing > 0) {
         final int bytesRead = is.read(b, currentOffset, bytesMissing);
         if (bytesRead == IOUtils.EOF)
            throw new EOFException("Unexpected end of input stream reached.");
         currentOffset += bytesRead;
         bytesMissing -= bytesRead;
      }
   }

   /**
    * Reads <code>len</code> bytes of data from the input stream into
    * an array of bytes. This method blocks until the given number of bytes could be read.
    *
    * @param len the exact number of bytes to read.
    */
   public static byte[] readBytes(final InputStream is, final int len) throws IOException {
      final var bytes = new byte[len];
      readBytes(is, bytes, 0, len);
      return bytes;
   }

   public static byte[] readBytesAndClose(final InputStream is) throws IOException {
      try (var os = new FastByteArrayOutputStream()) {
         copyLarge(is, os);
         return os.toByteArray();
      } finally {
         closeQuietly(is);
      }
   }

   public static String readChunkAsString(final InputStream is, final int maxSize) throws IOException {
      if (is.available() == 0)
         return "";
      final var buff = new byte[maxSize];
      final int readLen = is.read(buff);
      if (readLen == IOUtils.EOF)
         return "";
      return new String(buff, 0, readLen, Charset.defaultCharset());
   }

   /**
    * Reads an int value from the given input stream using the same way as {@link DataInputStream#readInt()}
    */
   public static int readInt(final InputStream is) throws IOException {
      final int ch1 = is.read();
      final int ch2 = is.read();
      final int ch3 = is.read();
      final int ch4 = is.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0)
         throw new EOFException("Unexpected end of input stream reached.");
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
   }

   public static List<String> readLines(final InputStream in) throws IOException {
      return readLines(new InputStreamReader(in));
   }

   public static List<String> readLines(final InputStream in, final Charset charset) throws IOException {
      return readLines(new InputStreamReader(in, charset));
   }

   public static List<String> readLines(final Reader reader) throws IOException {
      final BufferedReader bf = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
      final var lines = new ArrayList<String>();
      String line = bf.readLine();
      while (line != null) {
         lines.add(line);
         line = bf.readLine();
      }

      LOG.trace("Lines read from reader %s: %s", reader, lines);
      return lines;
   }

   /**
    * @param searchFor single string or an array of strings where one must match
    */
   public static CharSequence readUntilContainsAny(final InputStream is, final String... searchFor) throws IOException {
      LOG.trace("Reading from stream %s until it contains any of %s", is, searchFor);
      final var result = new StringBuilder();
      while (true) {
         final String output = readChunkAsString(is, 4096);
         LOG.trace("Current output from stream %s: %s", is, output);
         result.append(output);
         if (Strings.containsAny(result, searchFor)) {
            LOG.trace("One of %s was found in stream %s", searchFor, is);
            break;
         }
         Threads.sleep(100);
      }
      return result;
   }

   /**
    * Wraps the input stream in a {@link BufferedInputStream} except if it is already one or
    * if it is of type {@link ByteArrayInputStream} or {@link FastByteArrayInputStream}
    */
   public static InputStream toBufferedInputStream(final InputStream input) {
      if (input instanceof BufferedInputStream || input instanceof ByteArrayInputStream || input instanceof FastByteArrayInputStream)
         return input;
      return new BufferedInputStream(input);
   }

   /**
    * Wraps the input stream in a {@link BufferedInputStream} except if it is already one or
    * if it is of type {@link ByteArrayInputStream} or {@link FastByteArrayInputStream}
    */
   public static InputStream toBufferedInputStream(final InputStream input, final int blockSize) {
      if (input instanceof BufferedInputStream || input instanceof ByteArrayInputStream || input instanceof FastByteArrayInputStream)
         return input;
      return new BufferedInputStream(input, blockSize);
   }

   @SuppressWarnings("null")
   public static String toString(final InputStream input) throws IOException {
      return toString(input, Charset.defaultCharset());
   }

   /**
    * Writes the given value to the output stream the same way as {@link DataOutputStream#writeInt(int)}
    */
   public static void writeInt(final OutputStream os, final int value) throws IOException {
      os.write(value >>> 24 & 0xFF);
      os.write(value >>> 16 & 0xFF);
      os.write(value >>> 8 & 0xFF);
      os.write(value >>> 0 & 0xFF);
   }
}
