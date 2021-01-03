/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeInputStreamTest {

   @Test
   @SuppressWarnings("resource")
   public void testCompositeInputStream() throws IOException {
      final FastByteArrayInputStream bis1 = new FastByteArrayInputStream("Hello ".getBytes());
      final FastByteArrayInputStream bis2 = new FastByteArrayInputStream("World!".getBytes());

      assertThat(new String(IOUtils.readBytes(new CompositeInputStream(bis1, bis2)))).isEqualTo("Hello World!");
      bis1.reset();
      bis2.reset();

      assertThat(new String(IOUtils.readBytes(new CompositeInputStream(bis1, bis2), 5))).isEqualTo("Hello");
      bis1.reset();
      bis2.reset();

      final CompositeInputStream cis = new CompositeInputStream(bis1, bis2);
      assertThat(cis.skip(7)).isEqualTo(7);
      assertThat(new String(IOUtils.readBytes(cis))).isEqualTo("orld!");
   }
}
