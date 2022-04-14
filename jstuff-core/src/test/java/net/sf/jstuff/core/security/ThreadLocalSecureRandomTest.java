/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadLocalSecureRandomTest {

   @Test
   public void testThreadLocalSecureRandom() {
      final ThreadLocalSecureRandom rand = ThreadLocalSecureRandom.builder() //
         .reseedEvery(Duration.ofMinutes(30)) //
         .useStrongInstances(true) //
         .build();
      assertThat(rand).isNotNull();

      rand.nextBoolean();
   }
}
