/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SizeTest {

   @Test
   public void testSize() {
      assertThat(Size.ofBytes(0).getBytes().longValue()).isZero();
      assertThat(Size.ofKiB(0).getBytes().longValue()).isZero();
      assertThat(Size.ofMiB(0).getBytes().longValue()).isZero();
      assertThat(Size.ofGiB(0).getBytes().longValue()).isZero();
      assertThat(Size.ofTiB(0).getBytes().longValue()).isZero();

      assertThat(Size.ofBytes(1).getBytes().longValue()).isEqualTo(1L);
      assertThat(Size.ofKiB(1).getBytes().longValue()).isEqualTo(1L * 1024);
      assertThat(Size.ofMiB(1).getBytes().longValue()).isEqualTo(1L * 1024 * 1024);
      assertThat(Size.ofGiB(1).getBytes().longValue()).isEqualTo(1L * 1024 * 1024 * 1024);
      assertThat(Size.ofTiB(1).getBytes().longValue()).isEqualTo(1L * 1024 * 1024 * 1024 * 1024);

      assertThat(Size.ofBytes(1024).getBytes().longValue()).isEqualTo(1024L);
      assertThat(Size.ofKiB(1024).getBytes().longValue()).isEqualTo(1024L * 1024);
      assertThat(Size.ofMiB(1024).getBytes().longValue()).isEqualTo(1024L * 1024 * 1024);
      assertThat(Size.ofGiB(1024).getBytes().longValue()).isEqualTo(1024L * 1024 * 1024 * 1024);
      assertThat(Size.ofTiB(1024).getBytes().longValue()).isEqualTo(1024L * 1024 * 1024 * 1024 * 1024);
   }
}