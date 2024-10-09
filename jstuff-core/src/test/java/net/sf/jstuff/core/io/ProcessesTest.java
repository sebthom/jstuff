/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.io.Processes.ProcessWrapper;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ProcessesTest {

   @Test
   void testWithStdInput() throws IOException, InterruptedException {
      final var pwb = SystemUtils.IS_OS_WINDOWS ? Processes.builder("findstr").withArg("x*") : Processes.builder("cat");

      final var stdout = new StringBuilder();
      pwb.withInput("Hello World!\n");
      pwb.withRedirectOutput(stdout);
      pwb.withRedirectErrorToOutput();

      final var pw = pwb.start();

      // Wait for the process to exit
      pw.waitForExit(2, TimeUnit.SECONDS);

      // Assert the output and exit code
      assertThat(pw.exitStatus()).isZero();
      assertThat(stdout).hasToString("Hello World!\n");

   }

   @Test
   void testCaptureOutput() throws IOException, InterruptedException {
      final var out = new StringBuilder();
      final var err = new StringBuilder();
      final ProcessWrapper prc = Processes.builder("ping") //
         .withArg("127.0.0.1") //
         .withArgs(SystemUtils.IS_OS_WINDOWS ? "-n" : "-c", 2) //
         .withRedirectError(err) //
         .withRedirectOutput(out) //
         .start() //
         .waitForExit(5, TimeUnit.SECONDS) //
         .terminate(5, TimeUnit.SECONDS);
      assertThat(prc.getState()).isEqualTo(Processes.ProcessState.SUCCEEDED);
      assertThat(prc.exitStatus()).isZero();
      assertThat(err).isEmpty();
      assertThat(out).contains("127.0.0.1");
   }

   @Test
   void testNormalCompletion() throws IOException, InterruptedException {
      final var signal = new CountDownLatch(2);
      final ProcessWrapper prc = Processes.builder("ping") //
         .withArg("127.0.0.1") //
         .withArgs(SystemUtils.IS_OS_WINDOWS ? "-n" : "-c", 2) //
         .withRedirectError(System.err) //
         .withRedirectOutput(System.out) //
         .onExit(p -> signal.countDown()) //
         .start() //
         .onExit(p -> signal.countDown()) //
         .waitForExit(5, TimeUnit.SECONDS);
      assertThat(prc.getState()).isEqualTo(Processes.ProcessState.SUCCEEDED);
      assertThat(prc.exitStatus()).isZero();
      Threads.sleep(1000);
      assertThat(signal.await(5, TimeUnit.SECONDS)).isTrue();

      prc.terminate(1, TimeUnit.SECONDS) // NO-OP
         .waitForExit();
      assertThat(prc.getState()).isEqualTo(Processes.ProcessState.SUCCEEDED);
   }

   @Test
   void testProgrammaticTermination() throws IOException, InterruptedException {
      final var signal = new CountDownLatch(2);
      final ProcessWrapper prc = Processes.builder("ping") //
         .withArgs(SystemUtils.IS_OS_WINDOWS ? "-n" : "-c", 9999) //
         .withArg("127.0.0.1") //
         .withRedirectError(System.err) //
         .withRedirectOutput(System.out) //
         .onExit(p -> signal.countDown()) //
         .start() //
         .onExit(p -> signal.countDown()) //
         .waitForExit(50, TimeUnit.MILLISECONDS);
      assertThat(prc.getState()).isEqualTo(Processes.ProcessState.RUNNING);
      assertThat(signal.getCount()).isEqualTo(2);

      prc.terminate() //
         .waitForExit();
      assertThat(prc.getState()).isEqualTo(Processes.ProcessState.TERMINATED);
      assertThat(prc.exitStatus()).isNotZero();
      Threads.sleep(1000);
      assertThat(signal.await(5, TimeUnit.SECONDS)).isTrue();
   }
}
