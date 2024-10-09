/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ThreadLocalSecureRandomTest {

   @Test
   void testThreadLocalSecureRandom() {
      final ThreadLocalSecureRandom rand = ThreadLocalSecureRandom.builder() //
         .reseedEvery(Duration.ofMinutes(30)) //
         .useStrongInstances(true) //
         .build();
      assertThat(rand).isNotNull();

      rand.nextBoolean();
   }
}
