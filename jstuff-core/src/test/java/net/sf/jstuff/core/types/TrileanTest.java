/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

import static net.sf.jstuff.core.types.Trilean.FALSE;
import static net.sf.jstuff.core.types.Trilean.TRUE;
import static net.sf.jstuff.core.types.Trilean.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TrileanTest {

   @Test
   public void testNegate() {
      assertThat(TRUE.negate()).isEqualTo(FALSE);
      assertThat(FALSE.negate()).isEqualTo(TRUE);
      assertThat(UNKNOWN.negate()).isEqualTo(UNKNOWN);
   }

   @Test
   public void testAnd() {
      assertThat(TRUE.and(TRUE)).isEqualTo(TRUE);
      assertThat(TRUE.and(FALSE)).isEqualTo(FALSE);
      assertThat(TRUE.and(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(TRUE.and(null)).isEqualTo(UNKNOWN);

      assertThat(FALSE.and(TRUE)).isEqualTo(FALSE);
      assertThat(FALSE.and(FALSE)).isEqualTo(FALSE);
      assertThat(FALSE.and(UNKNOWN)).isEqualTo(FALSE);
      assertThat(FALSE.and(null)).isEqualTo(FALSE);

      assertThat(UNKNOWN.and(TRUE)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.and(FALSE)).isEqualTo(FALSE);
      assertThat(UNKNOWN.and(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.and(null)).isEqualTo(UNKNOWN);
   }

   @Test
   public void testOr() {
      assertThat(TRUE.or(TRUE)).isEqualTo(TRUE);
      assertThat(TRUE.or(FALSE)).isEqualTo(TRUE);
      assertThat(TRUE.or(UNKNOWN)).isEqualTo(TRUE);
      assertThat(TRUE.or(null)).isEqualTo(TRUE);

      assertThat(FALSE.or(TRUE)).isEqualTo(TRUE);
      assertThat(FALSE.or(FALSE)).isEqualTo(FALSE);
      assertThat(FALSE.or(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(FALSE.or(null)).isEqualTo(UNKNOWN);

      assertThat(UNKNOWN.or(TRUE)).isEqualTo(TRUE);
      assertThat(UNKNOWN.or(FALSE)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.or(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.or(null)).isEqualTo(UNKNOWN);
   }

   @Test
   public void testXor() {
      assertThat(TRUE.xor(TRUE)).isEqualTo(FALSE);
      assertThat(TRUE.xor(FALSE)).isEqualTo(TRUE);
      assertThat(TRUE.xor(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(TRUE.xor(null)).isEqualTo(UNKNOWN);

      assertThat(FALSE.xor(TRUE)).isEqualTo(TRUE);
      assertThat(FALSE.xor(FALSE)).isEqualTo(FALSE);
      assertThat(FALSE.xor(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(FALSE.xor(null)).isEqualTo(UNKNOWN);

      assertThat(UNKNOWN.xor(TRUE)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.xor(FALSE)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.xor(UNKNOWN)).isEqualTo(UNKNOWN);
      assertThat(UNKNOWN.xor(null)).isEqualTo(UNKNOWN);
   }
}
