/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DataInputInputStream extends InputStream {

   private final DataInput input;

   public DataInputInputStream(final DataInput input) {
      this.input = input;
   }

   @Override
   public int read() throws IOException {
      try {
         return input.readInt();
      } catch (final IndexOutOfBoundsException ex) {
         // e.g. in io.netty.buffer.AbstractByteBuf.readInt()
         return -1;
      } catch (final EOFException ex) {
         return -1;
      }
   }
}
