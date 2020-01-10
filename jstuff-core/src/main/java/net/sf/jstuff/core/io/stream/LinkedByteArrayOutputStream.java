/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized implementation of {@link java.io.ByteArrayOutputStream} that uses an internal linked list of byte blocks instead of resizing/copying a
 * single internal byte array.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LinkedByteArrayOutputStream extends OutputStream {

   private static final int DEFAULT_BLOCK_SIZE = 4096;

   private LinkedList<byte[]> blocks;
   private byte[] block;
   private final int blockSize;
   private int blockWritePos;

   public LinkedByteArrayOutputStream() {
      this(DEFAULT_BLOCK_SIZE);
   }

   public LinkedByteArrayOutputStream(final int blockSize) {
      Args.notNegative("blockSize", blockSize);
      this.blockSize = blockSize;
      block = new byte[blockSize];
   }

   /**
    * Closing a {@link LinkedByteArrayOutputStream} has no effect.
    */
   @Override
   public void close() {
      // nothing to do
   }

   /**
    * Flushing a {@link LinkedByteArrayOutputStream} has no effect.
    */
   @Override
   public void flush() {
      // nothing to do
   }

   protected void nextBlock() {
      if (blocks == null) {
         blocks = new LinkedList<byte[]>();
      }

      blocks.addLast(block);
      block = new byte[blockSize];
      blockWritePos = 0;
   }

   public void reset() {
      if (blocks != null) {
         blocks.clear();
      }
      blockWritePos = 0;
   }

   public int size() {
      return blocks.size() * blockSize + blockWritePos;
   }

   public byte[] toByteArray() {
      final byte[] result = new byte[size()];
      int resultWritePos = 0;

      if (blocks != null) {
         for (final byte[] block : blocks) {
            System.arraycopy(block, 0, result, resultWritePos, blockSize);
            resultWritePos += blockSize;
         }
      }

      System.arraycopy(block, 0, result, resultWritePos, blockWritePos);
      return result;
   }

   @Override
   public String toString() {
      return new String(toByteArray());
   }

   public String toString(final String charsetName) throws UnsupportedEncodingException {
      return new String(toByteArray(), charsetName);
   }

   @Override
   public void write(final byte[] b) {
      write(b, 0, b.length);
   }

   @Override
   public void write(final byte[] buf, int offset, int length) {
      if (offset < 0 || length < 0 || offset + length > buf.length)
         throw new IndexOutOfBoundsException();

      if (blockWritePos + length <= blockSize) {
         System.arraycopy(buf, offset, block, blockWritePos, length);
         blockWritePos += length;
         return;
      }

      do {
         if (blockWritePos == blockSize) {
            nextBlock();
         }

         int copyLength = blockSize - blockWritePos;
         if (length < copyLength) {
            copyLength = length;
         }

         System.arraycopy(buf, offset, block, blockWritePos, copyLength);
         blockWritePos += copyLength;
         offset += copyLength;
         length -= copyLength;
      }
      while (length > 0);
   }

   @Override
   public void write(final int b) throws IOException {
      if (blockWritePos == blockSize) {
         nextBlock();
      }

      block[blockWritePos++] = (byte) b;
   }

   public void writeTo(final OutputStream out) throws IOException {
      if (blocks != null) {
         for (final byte[] block : blocks) {
            out.write(block);
         }
      }
      out.write(block, 0, blockWritePos);
   }
}
