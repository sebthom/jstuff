/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.exception;

import java.io.IOException;
import java.security.GeneralSecurityException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExceptionsTest extends TestCase {

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

   public void testThrowSneakily() {
      final IOException ex = new IOException();
      try {
         Exceptions.throwSneakily(ex);
         fail();
      } catch (final Exception e) {
         assertEquals(ex, e);
      }

      try {
         throw Exceptions.throwSneakily(ex);
      } catch (final Exception e) {
         assertEquals(ex, e);
      }
   }

   public void testWrapAs() {
      final IOException ex = new IOException(new GeneralSecurityException());
      assertSame(ex, Exceptions.wrapAs(ex, IOException.class));
      assertSame(ex, Exceptions.wrapAs(ex, Exception.class));
      assertSame(ex, Exceptions.wrapAs(ex, Throwable.class));
      assertNotSame(ex, Exceptions.wrapAs(ex, GeneralSecurityException.class));

      final CustomException cex = Exceptions.wrapAs(ex, CustomException.class);
      assertEquals(ex.getCause(), cex.getCause().getCause());
      assertTrue(Exceptions.getStackTrace(ex) != Exceptions.getStackTrace(cex));

      final RuntimeException rex = Exceptions.wrapAsRuntimeException(ex);
      assertEquals(ex.getCause(), rex.getCause());
      assertEquals(Exceptions.getStackTrace(ex), Exceptions.getStackTrace(rex));
   }

}
