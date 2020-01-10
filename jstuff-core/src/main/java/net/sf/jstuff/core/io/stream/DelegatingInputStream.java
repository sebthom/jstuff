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
import java.io.InputStream;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingInputStream extends InputStream implements Decorator<InputStream> {

   protected InputStream wrapped;
   protected boolean ignoreClose = false;

   public DelegatingInputStream(final InputStream wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public int available() throws IOException {
      return wrapped.available();
   }

   @Override
   public void close() throws IOException {
      if (!ignoreClose) {
         wrapped.close();
      }
   }

   public boolean isIgnoreClose() {
      return ignoreClose;
   }

   @Override
   public void mark(final int readlimit) {
      wrapped.mark(readlimit);
   }

   @Override
   public boolean markSupported() {
      return wrapped.markSupported();
   }

   public byte readByte() throws IOException {
      final byte[] b = new byte[1];
      wrapped.read(b);
      return b[0];
   }

   @Override
   public int read() throws IOException {
      return wrapped.read();
   }

   @Override
   public int read(final byte[] b) throws IOException {
      return wrapped.read(b);
   }

   @Override
   public int read(final byte[] b, final int off, final int len) throws IOException {
      return wrapped.read(b, off, len);
   }

   @Override
   public void reset() throws IOException {
      wrapped.reset();
   }

   public void setIgnoreClose(final boolean ignoreClose) {
      this.ignoreClose = ignoreClose;
   }

   @Override
   public long skip(final long n) throws IOException {
      return wrapped.skip(n);
   }

   @Override
   public InputStream getWrapped() {
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
   public void setWrapped(final InputStream wrapped) {
      this.wrapped = wrapped;
   }

}
