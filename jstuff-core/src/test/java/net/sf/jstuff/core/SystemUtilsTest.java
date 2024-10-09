/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class SystemUtilsTest {

   @Test
   void testFindExecutable() {
      assertThat(SystemUtils.findExecutable("sort", false)).isNotNull();
      if (SystemUtils.IS_OS_WINDOWS) {
         assertThat(SystemUtils.findExecutable("sort.exe", false)).isNotNull();
      }
   }

   @Test
   void testSplitCommandLine() {
      assertThat(SystemUtils.splitCommandLine("")).isEmpty();
      assertThat(SystemUtils.splitCommandLine("  ")).isEmpty();
      assertThat(SystemUtils.splitCommandLine(" echo ")).containsExactly("echo");
      assertThat(SystemUtils.splitCommandLine("echo 'hello'")).containsExactly("echo", "hello");
      assertThat(SystemUtils.splitCommandLine("echo 'hello \"world\"'")).containsExactly("echo", "hello \"world\"");
      assertThat(SystemUtils.splitCommandLine("echo \"hello 'world'\"")).containsExactly("echo", "hello 'world'");
      assertThat(SystemUtils.splitCommandLine("echo 'it\\'s cool'")).containsExactly("echo", "it's cool");
      assertThat(SystemUtils.splitCommandLine("echo hello\\ world ")).containsExactly("echo", "hello world");
   }
}
