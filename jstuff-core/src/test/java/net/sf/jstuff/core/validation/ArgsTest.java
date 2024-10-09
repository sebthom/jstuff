/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.validation;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ArgsTest {

   @Test
   void testArgs_NotEmpty() {
      try {
         Args.notEmpty("password", (String) null);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).isEqualTo("[password] must not be null");
      }

      try {
         Args.notEmpty("password", "");
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).isEqualTo("[password] must not be empty");
      }

      Args.notEmpty("password", "secret");

      try {
         Args.notEmpty("values", (String[]) null);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).isEqualTo("[values] must not be null");
      }

      try {
         Args.notEmpty("values", new String[0]);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).isEqualTo("[values] must not be empty");
      }

      Args.notEmpty("values", new String[] {"dfd"});

   }

   @Test
   void testArgs_NotNull() {
      try {
         Args.notNull("password", null);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).isEqualTo("[password] must not be null");
      }

      Args.notNull("password", "");
      Args.notNull("password", "secret");
   }

   @Test
   void testArgs_IsFileReadable() throws IOException {
      try {
         Args.isFileReadable("file", new File("foo"));
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).contains("does not exist");
      }

      try {
         Args.isFileReadable("file", File.createTempFile("foo", "bar").getParentFile());
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException ex) {
         assertThat(ex.getMessage()).contains("is not a regular file");
      }
   }
}
