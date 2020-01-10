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
public class AssertTest extends TestCase {

   public void testAssert_IsFalse() {
      try {
         Assert.isFalse(true, "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      Assert.isFalse(false, "foo");
   }

   public void testAssert_IsFileReadable() throws IOException {
      try {
         Assert.isFileReadable(new File("foo"));
         fail();
      } catch (final IllegalStateException ex) {
         assertTrue(ex.getMessage().contains("does not exist"));
      }

      try {
         Assert.isFileReadable(File.createTempFile("foo", "bar").getParentFile());
         fail();
      } catch (final IllegalStateException ex) {
         assertTrue(ex.getMessage().contains("is not a regular file"));
      }
   }

   public void testAssert_IsTrue() {
      try {
         Assert.isTrue(false, "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      Assert.isTrue(true, "foo");
   }

   public void testAssert_NotEmpty() {
      try {
         Assert.notEmpty("", "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      try {
         Assert.notEmpty((String) null, "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      Assert.notEmpty("value", "foo");

      try {
         Assert.notEmpty(new String[0], "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      try {
         Assert.notEmpty((String[]) null, "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      Assert.notEmpty(new String[] {"value"}, "foo");
   }

   public void testAssert_NotNull() {
      try {
         Assert.notNull(null, "foo");
         fail();
      } catch (final IllegalStateException ex) {
         assertEquals("foo", ex.getMessage());
      }

      Assert.notNull("value", "foo");
   }
}
