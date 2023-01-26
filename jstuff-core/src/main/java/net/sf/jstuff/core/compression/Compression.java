/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Compression {

   byte[] compress(byte[] uncompressed) throws IOException;

   void compress(byte[] uncompressed, OutputStream output) throws IOException;

   void compress(InputStream uncompressed, OutputStream output) throws IOException;

   InputStream createCompressingInputStream(byte[] uncompressed) throws IOException;

   InputStream createCompressingInputStream(InputStream uncompressed) throws IOException;

   OutputStream createCompressingOutputStream(OutputStream output) throws IOException;

   InputStream createDecompressingInputStream(byte[] compressed) throws IOException;

   InputStream createDecompressingInputStream(InputStream compressed) throws IOException;

   byte[] decompress(byte[] compressed) throws IOException;

   int decompress(byte[] compressed, byte[] output) throws IOException;

   void decompress(byte[] compressed, OutputStream output) throws IOException;

   void decompress(InputStream compressed, OutputStream output) throws IOException;
}
