/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.validation;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArgsTest extends TestCase {

   public void testArgs_NotEmpty() {
      try {
         Args.notEmpty("password", (String) null);
         fail();
      } catch (final IllegalArgumentException ex) {
         assertEquals("[password] must not be null", ex.getMessage());
      }

      try {
         Args.notEmpty("password", "");
         fail();
      } catch (final IllegalArgumentException ex) {
         assertEquals("[password] must not be empty", ex.getMessage());
      }

      Args.notEmpty("password", "secret");

      try {
         Args.notEmpty("values", (String[]) null);
         fail();
      } catch (final IllegalArgumentException ex) {
         assertEquals("[values] must not be null", ex.getMessage());
      }

      try {
         Args.notEmpty("values", new String[0]);
         fail();
      } catch (final IllegalArgumentException ex) {
         assertEquals("[values] must not be empty", ex.getMessage());
      }

      Args.notEmpty("values", new String[] {"dfd"});

   }

   public void testArgs_NotNull() {
      try {
         Args.notNull("password", null);
         fail();
      } catch (final IllegalArgumentException ex) {
         assertEquals("[password] must not be null", ex.getMessage());
      }

      Args.notNull("password", "");
      Args.notNull("password", "secret");
   }

   public void testArgs_IsFileReadable() throws IOException {
      try {
         Args.isFileReadable("file", new File("foo"));
         fail();
      } catch (final IllegalArgumentException ex) {
         assertTrue(ex.getMessage().contains("does not exist"));
      }

      try {
         Args.isFileReadable("file", File.createTempFile("foo", "bar").getParentFile());
         fail();
      } catch (final IllegalArgumentException ex) {
         assertTrue(ex.getMessage().contains("is not a regular file"));
      }
   }
}
