/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.io.IOException;
import java.util.zip.Deflater;

import junit.framework.TestCase;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.io.stream.ZippedBlockInputStream;
import net.sf.jstuff.core.io.stream.ZippedBlockOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockStreamTest extends TestCase {
   @SuppressWarnings("resource")
   public void testZippedBlockStream() throws IOException {
      final FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
      final ZippedBlockOutputStream zos = new ZippedBlockOutputStream(bos, 16, Deflater.BEST_SPEED);
      zos.write("Hello World! Hello World!".getBytes());
      zos.flush();

      final FastByteArrayInputStream bis = new FastByteArrayInputStream(bos.toByteArray());
      final ZippedBlockInputStream zis = new ZippedBlockInputStream(bis);
      assertEquals(12, IOUtils.readBytes(zis, 12).length);

      assertEquals(" Hello World!", new String(IOUtils.readBytes(zis, 13)));
   }
}
