/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.tuple;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TupleTest {

   @Test
   public void testTuple1() {
      final Tuple1<String> t1 = Tuple1.create("a");
      final Tuple1<String> t2 = Tuple1.create("b");
      final Tuple1<String> t3 = Tuple1.create("a");
      assertThat(t1).isNotEqualTo(t2);
      assertThat(t3).isEqualTo(t1);
   }

   @Test
   public void testTuple2() {
      final Tuple2<String, String> t1 = Tuple2.create("a", "a");
      final Tuple2<String, String> t2 = Tuple2.create("b", "a");
      final Tuple2<String, String> t3 = Tuple2.create("a", "a");
      assertThat(t1).isNotEqualTo(t2);
      assertThat(t3).isEqualTo(t1);
   }

   @Test
   public void testTuple3() {
      final Tuple3<String, String, String> t1 = Tuple3.create("a", "a", "a");
      final Tuple3<String, String, String> t2 = Tuple3.create("b", "a", "a");
      final Tuple3<String, String, String> t3 = Tuple3.create("a", "a", "a");
      assertThat(t1).isNotEqualTo(t2);
      assertThat(t3).isEqualTo(t1);
   }

   @Test
   public void testTuple4() {
      final Tuple4<String, String, String, String> t1 = Tuple4.create("a", "a", "a", "a");
      final Tuple4<String, String, String, String> t2 = Tuple4.create("b", "a", "a", "a");
      final Tuple4<String, String, String, String> t3 = Tuple4.create("a", "a", "a", "a");
      assertThat(t1).isNotEqualTo(t2);
      assertThat(t3).isEqualTo(t1);
   }

   @Test
   public void testTuple5() {
      final Tuple5<String, String, String, String, String> t1 = Tuple5.create("a", "a", "a", "a", "a");
      final Tuple5<String, String, String, String, String> t2 = Tuple5.create("b", "a", "a", "a", "a");
      final Tuple5<String, String, String, String, String> t3 = Tuple5.create("a", "a", "a", "a", "a");
      assertThat(t1).isNotEqualTo(t2);
      assertThat(t3).isEqualTo(t1);
   }
}
