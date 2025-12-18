/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
   void testOnExitWaitsForRedirects() throws Exception {
      final String exe;
      final List<String> args;
      if (SystemUtils.IS_OS_WINDOWS) {
         exe = "cmd";
         args = List.of("/c", "for /L %i in (1,1,50) do @echo LINE-%i");
      } else {
         exe = "sh";
         args = List.of("-c", "i=1; while [ $i -le 50 ]; do echo LINE-$i; i=$((i+1)); done");
      }

      final var stdout = new StringBuilder();
      final var linesSeen = new AtomicInteger();

      final ProcessWrapper prc = Processes.builder(exe) //
         .withArgs(args) //
         .withRedirectOutput(line -> {
            stdout.append(line).append(System.lineSeparator());
            linesSeen.incrementAndGet();
            Threads.sleep(5);
         }) //
         .start();

      prc.onExit().get(5, TimeUnit.SECONDS);

      assertThat(prc.getState()).isEqualTo(Processes.ProcessState.SUCCEEDED);
      assertThat(linesSeen.get()).isEqualTo(50);
      assertThat(stdout).contains("LINE-50");
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

   @Test
   void testRedirectOutputToFile() throws IOException, InterruptedException {
      final Path outFile = Files.createTempFile("jstuff-processes-out", ".txt");
      try {
         final String exe;
         final List<String> args;
         if (SystemUtils.IS_OS_WINDOWS) {
            exe = "cmd";
            args = List.of("/c", "echo OUT-FILE");
         } else {
            exe = "sh";
            args = List.of("-c", "echo OUT-FILE");
         }

         final ProcessWrapper prc = Processes.builder(exe) //
            .withArgs(args) //
            .withRedirectOutput(outFile) //
            .start() //
            .waitForExit(5, TimeUnit.SECONDS);

         assertThat(prc.getState()).isEqualTo(Processes.ProcessState.SUCCEEDED);
         assertThat(prc.exitStatus()).isZero();

         final String content = Files.readString(outFile);
         assertThat(content).contains("OUT-FILE");
      } finally {
         Files.deleteIfExists(outFile);
      }
   }

   @Test
   void testRedirectErrorToOutputFile() throws IOException, InterruptedException {
      final Path outFile = Files.createTempFile("jstuff-processes-errout", ".txt");
      try {
         final String exe;
         final List<String> args;
         if (SystemUtils.IS_OS_WINDOWS) {
            exe = "cmd";
            args = List.of("/c", "echo OUT-LINE & echo ERR-LINE 1>&2");
         } else {
            exe = "sh";
            args = List.of("-c", "echo OUT-LINE; echo ERR-LINE 1>&2");
         }

         final ProcessWrapper prc = Processes.builder(exe) //
            .withArgs(args) //
            .withRedirectErrorToOutput() //
            .withRedirectOutput(outFile) //
            .start() //
            .waitForExit(5, TimeUnit.SECONDS);

         assertThat(prc.getState()).isEqualTo(Processes.ProcessState.SUCCEEDED);
         assertThat(prc.exitStatus()).isZero();

         final String content = Files.readString(outFile);
         assertThat(content).contains("OUT-LINE");
         assertThat(content).contains("ERR-LINE");
      } finally {
         Files.deleteIfExists(outFile);
      }
   }
}
