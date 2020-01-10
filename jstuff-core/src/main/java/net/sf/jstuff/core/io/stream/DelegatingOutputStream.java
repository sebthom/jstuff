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

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingOutputStream extends OutputStream implements Decorator<OutputStream> {

   protected OutputStream wrapped;
   protected boolean ignoreClose = false;

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
