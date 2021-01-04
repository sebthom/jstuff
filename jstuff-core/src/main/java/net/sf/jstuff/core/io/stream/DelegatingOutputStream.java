/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.OutputStream;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingOutputStream extends OutputStream implements Decorator<OutputStream> {

   protected OutputStream wrapped;
   protected boolean ignoreClose;

   public DelegatingOutputStream(final OutputStream wrapped) {
      this.wrapped = wrapped;
   }

   public DelegatingOutputStream(final OutputStream wrapped, final boolean ignoreClose) {
      this.wrapped = wrapped;
      this.ignoreClose = ignoreClose;
   }

   @Override
   public void close() throws IOException {
      if (!ignoreClose) {
         wrapped.close();
      }
   }

   @Override
   public void flush() throws IOException {
      wrapped.flush();
   }

   public boolean isIgnoreClose() {
      return ignoreClose;
   }

   public void setIgnoreClose(final boolean ignoreClose) {
      this.ignoreClose = ignoreClose;
   }

   @Override
   public void write(final byte[] bytes) throws IOException {
      wrapped.write(bytes);
   }

   @Override
   public void write(final byte[] bytes, final int i, final int i1) throws IOException {
      wrapped.write(bytes, i, i1);
   }

   @Override
   public void write(final int i) throws IOException {
      wrapped.write(i);
   }

   public void writeByte(final byte b) throws IOException {
      wrapped.write(new byte[] {b});
   }

   @Override
   public OutputStream getWrapped() {
      return wrapped;
   }

   @Override
   public boolean isWrappedGettable() {
      return true;
   }

   @Override
   public boolean isWrappedSettable() {
      return true;

   }

   @Override
   public void setWrapped(final OutputStream wrapped) {
      this.wrapped = wrapped;
   }
}
