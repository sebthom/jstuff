/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.exception;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExceptionsTest {

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
   public void testThrowSneakily() {
      final IOException ex = new IOException();
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
   public void testWrapAs() {
      final IOException ex = new IOException(new GeneralSecurityException());
      assertThat(Exceptions.wrapAs(ex, IOException.class)).isSameAs(ex);
      assertThat(Exceptions.wrapAs(ex, Exception.class)).isSameAs(ex);
      assertThat(Exceptions.wrapAs(ex, Throwable.class)).isSameAs(ex);
      assertThat(Exceptions.wrapAs(ex, GeneralSecurityException.class)).isNotSameAs(ex);

      final CustomException cex = Exceptions.wrapAs(ex, CustomException.class);
      assertThat(cex.getCause().getCause()).isEqualTo(ex.getCause());
      assertThat(Exceptions.getStackTrace(cex)).isNotSameAs(Exceptions.getStackTrace(ex));

      final RuntimeException rex = Exceptions.wrapAsRuntimeException(ex);
      assertThat(rex.getCause()).isEqualTo(ex.getCause());
      assertThat(Exceptions.getStackTrace(rex)).isEqualTo(Exceptions.getStackTrace(ex));
   }
}
