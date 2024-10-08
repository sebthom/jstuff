/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.exception;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ExceptionsTest {

   private static final class CustomException extends Exception {

      private static final long serialVersionUID = 1L;

      private CustomException(final String message) {
         super(message);
      }

      private CustomException(final String message, final Throwable cause) {
         super(message, cause);
      }

      private CustomException(final Throwable cause) {
         super(cause);
      }
   }

   @Test
   void testEquals() {
      final var ex = new Throwable[2];

      for (int i = 0; i < 2; i++) {
         ex[i] = new CustomException("foo");
      }

      assertThat(Exceptions.equals(null, null)).isTrue();
      assertThat(Exceptions.equals(ex[0], null)).isFalse();
      assertThat(Exceptions.equals(null, ex[0])).isFalse();
      assertThat(Exceptions.equals(ex[0], ex[0])).isTrue();
      assertThat(Exceptions.equals(ex[0], ex[1])).isTrue();
      assertThat(Exceptions.equals(ex[0], new CustomException("foo"))).isFalse();
   }

   @Test
   void testThrowSneakily() {
      final var ex = new IOException();
      try {
         Exceptions.throwSneakily(ex);
         failBecauseExceptionWasNotThrown(IOException.class);
      } catch (final Exception e) {
         assertThat(e).isEqualTo(ex);
      }

      try {
         throw Exceptions.throwSneakily(ex);
      } catch (final Exception e) {
         assertThat(e).isEqualTo(ex);
      }
   }

   @Test
   void testWrapAs() {
      final var ex = new IOException(new GeneralSecurityException());
      assertThat(Exceptions.wrapAs(ex, IOException.class)).isSameAs(ex);
      assertThat(Exceptions.wrapAs(ex, Exception.class)).isSameAs(ex);
      assertThat(Exceptions.wrapAs(ex, Throwable.class)).isSameAs(ex);
      assertThat(Exceptions.wrapAs(ex, GeneralSecurityException.class)).isNotSameAs(ex);

      final CustomException cex = Exceptions.wrapAs(ex, CustomException.class);
      assertThat(asNonNull(cex.getCause()).getCause()).isEqualTo(ex.getCause());
      assertThat(Exceptions.getStackTrace(cex)).isNotSameAs(Exceptions.getStackTrace(ex));

      final RuntimeException rex = Exceptions.wrapAsRuntimeException(ex);
      assertThat(rex.getCause()).isEqualTo(ex.getCause());
      assertThat(Exceptions.getStackTrace(rex)).isEqualTo(Exceptions.getStackTrace(ex));
   }
}
