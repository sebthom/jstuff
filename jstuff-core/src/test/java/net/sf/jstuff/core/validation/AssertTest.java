/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.validation;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AssertTest {

   @Test
   public void testAssert_IsFalse() {
      try {
         Assert.isFalse(true, "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      Assert.isFalse(false, "foo");
   }

   @Test
   public void testAssert_IsFileReadable() throws IOException {
      try {
         Assert.isFileReadable(new File("foo"));
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).contains("does not exist");
      }

      try {
         Assert.isFileReadable(File.createTempFile("foo", "bar").getParentFile());
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).contains("is not a regular file");
      }
   }

   @Test
   public void testAssert_IsTrue() {
      try {
         Assert.isTrue(false, "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      Assert.isTrue(true, "foo");
   }

   @Test
   public void testAssert_NotEmpty() {
      try {
         Assert.notEmpty("", "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      try {
         Assert.notEmpty((String) null, "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      Assert.notEmpty("value", "foo");

      try {
         Assert.notEmpty(new String[0], "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      try {
         Assert.notEmpty((String[]) null, "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      Assert.notEmpty(new String[] {"value"}, "foo");
   }

   @Test
   public void testAssert_NotNull() {
      try {
         Assert.notNull(null, "foo");
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).isEqualTo("foo");
      }

      Assert.notNull("value", "foo");
   }
}
