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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class IOUtilsTest {

   @Test
   public void testReadBytes() throws IOException {
      final ByteArrayInputStream is = new ByteArrayInputStream("Hello World!".getBytes());
      assertThat(IOUtils.readBytes(is, 5)).hasSize(5);

      is.reset();
      assertThat(new String(IOUtils.readBytes(is, 12))).isEqualTo("Hello World!");
   }
}
