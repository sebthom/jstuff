/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteBufferInputStream extends InputStream {

   private final ByteBuffer buf;

   public ByteBufferInputStream(final ByteBuffer buf) {
      this.buf = buf;
   }

   @Override
   public int read() throws IOException {
      if (!buf.hasRemaining())
         return -1;
      return buf.get() & 0xFF;
   }

   @Override
   public int read(final byte[] bytes, final int off, int len) throws IOException {
      if (!buf.hasRemaining())
         return -1;

      len = Math.min(len, buf.remaining());
      buf.get(bytes, off, len);
      return len;
   }
}
