/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.RuntimeIOException;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * An InputStream that compresses data from an underlying input stream while reading.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class GZIPCompressingInputStream extends DeflaterInputStream {

   static {
      try {
         final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
         final GZIPOutputStream gzip = new GZIPOutputStream(out, 16);
         GZIP_HEADER = out.toByteArray();
         gzip.close();
      } catch (final IOException ex) {
         throw new RuntimeIOException(ex);
      }
   }

   private static final byte[] GZIP_HEADER;

   /**
    * buffer that either holds the gzip header or trailer
    */
   private byte[] buf = GZIP_HEADER;
   private int bufPos;
   private int bufLen = GZIP_HEADER.length;
   private boolean bufContainsHeader = true;

   public GZIPCompressingInputStream(final InputStream source) {
      super(new CheckedInputStream(source, new CRC32()), new Deflater(Deflater.DEFAULT_COMPRESSION, true));
   }

   public GZIPCompressingInputStream(final InputStream source, final int compressionLevel) {
      super(new CheckedInputStream(source, new CRC32()), new Deflater(compressionLevel, true));
   }

   public GZIPCompressingInputStream(final InputStream source, final int compressionLevel, final int bufSize) {
      super(new CheckedInputStream(source, new CRC32()), new Deflater(compressionLevel, true), bufSize);
   }

   @Override
   public int read(final byte[] bufCompressed, final int off, final int bytesToRead) throws IOException {
      if (off < 0 || bytesToRead < 0 || bytesToRead > bufCompressed.length - off)
         throw new IndexOutOfBoundsException();

      int bytesRead;

      // some data (header or trailer) left in buf? return data from buf
      if (bufLen - bufPos > 0) {
         bytesRead = Math.min(bytesToRead, bufLen - bufPos);
         System.arraycopy(buf, bufPos, bufCompressed, off, bytesRead);
         bufPos += bytesRead;
         return bytesRead;
      }

      bytesRead = super.read(bufCompressed, off, bytesToRead);
      if (bytesRead > 0)
         return bytesRead;

      // underlying input stream EOF? put trailer into buf and read from buf
      if (bytesRead <= IOUtils.EOF && bufContainsHeader) {
         buf = new byte[8]; // 1x int á 4 byte, 2 x short á 2 byte
         bufPos = 0;
         bufLen = buf.length;
         writeTrailer(buf, bufPos);
         bufContainsHeader = false;
         return read(bufCompressed, off, bytesToRead);
      }
      return bytesRead;
   }

   private void writeInt(final int i, final byte[] buf, final int offset) {
      writeShort(i & 0xffff, buf, offset);
      writeShort(i >> 16 & 0xffff, buf, offset + 2);
   }

   private void writeShort(final int s, final byte[] buf, final int offset) {
      buf[offset] = (byte) (s & 0xff);
      buf[offset + 1] = (byte) (s >> 8 & 0xff);
   }

   private void writeTrailer(final byte[] buf, final int offset) {
      writeInt((int) ((CheckedInputStream) in).getChecksum().getValue(), buf, offset); // CRC-32 of uncompr. data
      writeInt(asNonNull(compressor).getTotalIn(), buf, offset + 4); // Number of uncompr. bytes
   }
}
