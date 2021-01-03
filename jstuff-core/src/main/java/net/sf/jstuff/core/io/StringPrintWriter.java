/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.io.PrintWriter;

import org.apache.commons.io.output.StringBuilderWriter;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringPrintWriter extends PrintWriter {

   @SuppressWarnings("resource")
   public StringPrintWriter() {
      super(new StringBuilderWriter());
   }

   @SuppressWarnings("resource")
   public StringPrintWriter(final int initialSize) {
      super(new StringBuilderWriter(initialSize));
   }

   @Override
   public String toString() {
      flush();
      return ((StringBuilderWriter) out).toString();
   }
}
