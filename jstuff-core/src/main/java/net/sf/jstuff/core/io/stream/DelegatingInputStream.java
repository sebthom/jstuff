/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingInputStream extends InputStream {

   protected InputStream delegate;
   protected boolean ignoreClose = false;

   public DelegatingInputStream(final InputStream delegate) {
      this.delegate = delegate;
   }

   @Override
   public int available() throws IOException {
      return delegate.available();
   }

   @Override
   public void close() throws IOException {
      if (!ignoreClose) {
         delegate.close();
      }
   }

   public InputStream getDelegate() {
      return delegate;
   }

   public boolean isIgnoreClose() {
      return ignoreClose;
   }

   @Override
   public void mark(final int readlimit) {
      delegate.mark(readlimit);
   }

   @Override
   public boolean markSupported() {
      return delegate.markSupported();
   }

   public byte readByte() throws IOException {
      final byte[] b = new byte[1];
      delegate.read(b);
      return b[0];
   }

   @Override
   public int read() throws IOException {
      return delegate.read();
   }

   @Override
   public int read(final byte[] b) throws IOException {
      return delegate.read(b);
   }

   @Override
   public int read(final byte[] b, final int off, final int len) throws IOException {
      return delegate.read(b, off, len);
   }

   @Override
   public void reset() throws IOException {
      delegate.reset();
   }

   public void setDelegate(final InputStream delegate) {
      this.delegate = delegate;
   }

   public void setIgnoreClose(final boolean ignoreClose) {
      this.ignoreClose = ignoreClose;
   }

   @Override
   public long skip(final long n) throws IOException {
      return delegate.skip(n);
   }

}
