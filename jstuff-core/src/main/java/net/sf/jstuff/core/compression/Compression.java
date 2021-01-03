/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Compression {

   byte[] compress(byte[] uncompressed) throws IOException;

   /**
    * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
    */
   void compress(byte[] uncompressed, OutputStream output, boolean closeOutput) throws IOException;

   /**
    * @param uncompressed will be closed
    * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
    */
   void compress(InputStream uncompressed, OutputStream output, boolean closeOutput) throws IOException;

   InputStream createCompressingInputStream(byte[] uncompressed) throws IOException;

   InputStream createCompressingInputStream(InputStream uncompressed) throws IOException;

   OutputStream createCompressingOutputStream(OutputStream output) throws IOException;

   InputStream createDecompressingInputStream(byte[] compressed) throws IOException;

   InputStream createDecompressingInputStream(InputStream compressed) throws IOException;

   byte[] decompress(byte[] compressed) throws IOException;

   int decompress(byte[] compressed, byte[] output) throws IOException;

   /**
    * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
    */
   void decompress(byte[] compressed, OutputStream output, boolean closeOutput) throws IOException;

   /**
    * @param compressed will be closed
    * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
    */
   void decompress(InputStream compressed, OutputStream output, boolean closeOutput) throws IOException;
}
