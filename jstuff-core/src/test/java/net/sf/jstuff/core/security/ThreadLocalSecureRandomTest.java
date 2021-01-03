/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.assertThat;

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
