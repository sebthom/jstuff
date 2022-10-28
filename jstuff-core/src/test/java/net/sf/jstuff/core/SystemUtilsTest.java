/*
 * Copyright 2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SystemUtilsTest {

   @Test
   public void testFindExecutable() {
      assertThat(SystemUtils.findExecutable("sort", false)).isNotNull();
      assertThat(SystemUtils.findExecutable("sort.exe", false)).isNotNull();
   }
}
