/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class EitherTest {

   @Test
   void testEitherLeft() {
      final Either<String, Integer> either = Either.left("foo");

      assertThat(either.value()).isEqualTo("foo");
      final String value = either.map(left -> "left", right -> "right");
      assertThat(value).isEqualTo("left");

      assertThat(either.left()).isEqualTo("foo");
      assertThat(either.isLeft()).isTrue();
      assertThat(either.leftOrElse("bar")).isEqualTo("foo");
      assertThat(either.mapLeft(left -> "bar")).isEqualTo(Either.left("bar"));

      assertThatThrownBy(() -> either.right()).isInstanceOf(IllegalStateException.class);
      assertThat(either.isRight()).isFalse();
      assertThat(either.rightOrElse(100)).isEqualTo(100);
      assertThat(either.mapRight(right -> "bar")).isEqualTo(either);
   }

   @Test
   void testEitherRight() {
      final Either<Integer, String> either = Either.right("foo");

      assertThat(either.value()).isEqualTo("foo");
      final String value = either.map(left -> "left", right -> "right");
      assertThat(value).isEqualTo("right");

      assertThatThrownBy(() -> either.left()).isInstanceOf(IllegalStateException.class);
      assertThat(either.isLeft()).isFalse();
      assertThat(either.leftOrElse(100)).isEqualTo(100);
      assertThat(either.mapLeft(left -> "bar")).isEqualTo(either);

      assertThat(either.right()).isEqualTo("foo");
      assertThat(either.isRight()).isTrue();
      assertThat(either.rightOrElse("bar")).isEqualTo("foo");
      assertThat(either.mapRight(right -> "bar")).isEqualTo(Either.right("bar"));
   }
}
