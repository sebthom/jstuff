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

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExceptionsTest extends TestCase {

   public void testThrowUnchecked() {
      try {
         Exceptions.throwUnchecked(new IOException());
         fail();
      } catch (final RuntimeException ex) {
         assertEquals(IOException.class, ex.getCause().getClass());
      }

      try {
         throw Exceptions.throwUnchecked(new IOException());
      } catch (final RuntimeException ex) {
         assertEquals(IOException.class, ex.getCause().getClass());
      }

      try {
         Exceptions.throwUncheckedRaw(new IOException());
         fail();
      } catch (final Exception ex) {
         assertEquals(IOException.class, ex.getClass());
      }

      try {
         throw Exceptions.throwUncheckedRaw(new IOException());
      } catch (final Exception ex) {
         assertEquals(IOException.class, ex.getClass());
      }
   }

   public void testWrapAs() {
      final Exception ex = new IOException();
      assertSame(ex, Exceptions.wrapAs(ex, Exception.class));
      assertEquals(RuntimeException.class, Exceptions.wrapAs(ex, RuntimeException.class).getClass());
      assertEquals(ex, Exceptions.wrapAs(ex, RuntimeException.class).getCause());
      ex.printStackTrace();
   }
}
