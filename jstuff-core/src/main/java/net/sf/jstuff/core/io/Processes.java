/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.SystemUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Processes {

   public static class Builder {

      private String executable;
      private final List<Object> args = new ArrayList<>(2);
      private Map<String, Object> env;
      private Function<Object, String> stringifier = Objects::toString;
      private File workDir;

      private boolean redirectErrorToOutput;
      private Object redirectError;
      private Object redirectOutput;
      private Consumer<ProcessWrapper> onExit;

      protected Builder(final String exe) {
         executable = exe;
      }

      private void assertRedirectErrorToOuputNotConfigured() {
         if (redirectErrorToOutput)
            throw new IllegalArgumentException("withRedirectError() and withRedirectErrorToOutput() are mutually exclusive.");
      }

      public Builder onExit(final Consumer<ProcessWrapper> action) {
         onExit = action;
         return this;
      }

      private CompletableFuture<Void> redirect(final InputStream in, final Appendable consumer) {
         return CompletableFuture.runAsync(() -> {
            try {
               IOUtils.copy(new InputStreamReader(in, StandardCharsets.UTF_8), consumer);
            } catch (final IOException ex) {
               throw new RuntimeIOException(ex);
            }
         }, BACKGROUND_THREADS);
      }

      private CompletableFuture<Void> redirect(final InputStream in, final Consumer<String> lineConsumer) {
         return CompletableFuture.runAsync(() -> {
            try (Scanner sc = new Scanner(in)) {
               while (sc.hasNextLine()) {
                  lineConsumer.accept(sc.nextLine());
               }
            }
         }, BACKGROUND_THREADS);
      }

      private CompletableFuture<Void> redirect(final InputStream in, final OutputStream out) {
         return CompletableFuture.runAsync(() -> {
            try {
               IOUtils.copy(in, out);
            } catch (final IOException ex) {
               throw new RuntimeIOException(ex);
            }
         }, BACKGROUND_THREADS);
      }

      /**
       * Runs the command in the background and immediately returns.
       */
      @SuppressWarnings({"resource", "unchecked"})
      public ProcessWrapper start() throws IOException {
         final List<String> command = CollectionUtils.newArrayList(executable);
         for (final Object arg : args) {
            command.add(stringifier.apply(arg));
         }

         final ProcessBuilder pb = new ProcessBuilder(command);
         if (env != null) {
            final Map<String, String> pbEnv = pb.environment();
            pbEnv.clear();
            env.forEach((k, v) -> pbEnv.put(k, stringifier.apply(v)));
         }

         if (workDir != null) {
            pb.directory(workDir);
         }

         final Process proc = pb.start();
         if (redirectErrorToOutput) {
            pb.redirectError();
         } else if (redirectError != null) {
            if (redirectError instanceof File) {
               pb.redirectError((File) redirectError);
            } else if (redirectError instanceof OutputStream) {
               redirect(proc.getErrorStream(), (OutputStream) redirectError);
            } else if (redirectError instanceof Appendable) {
               redirect(proc.getErrorStream(), (Appendable) redirectError);
            } else {
               redirect(proc.getErrorStream(), (Consumer<String>) redirectError);
            }
         }

         if (redirectOutput != null) {
            if (redirectOutput instanceof File) {
               pb.redirectOutput((File) redirectOutput);
            } else if (redirectOutput instanceof OutputStream) {
               redirect(proc.getInputStream(), (OutputStream) redirectOutput);
            } else if (redirectOutput instanceof Appendable) {
               redirect(proc.getInputStream(), (Appendable) redirectOutput);
            } else {
               redirect(proc.getInputStream(), (Consumer<String>) redirectOutput);
            }
         }
         return new ProcessWrapper(proc) //
            .onExit(onExit);
      }

      @Override
      public String toString() {
         return Processes.class.getSimpleName() + '.' + Strings.toString(this, //
            "executable", executable, //
            "args", args, //
            "workDir", workDir //
         );
      }

      public Builder withArg(final Object arg) {
         Args.notNull("arg", arg);
         args.add(arg.toString());
         return this;
      }

      public Builder withArgs(final List<Object> args) {
         Args.notNull("args", args);
         this.args.addAll(args);
         return this;
      }

      public Builder withArgs(final Object... args) {
         Args.notNull("args", args);
         Collections.addAll(this.args, args);
         return this;
      }

      public Builder withArgs(final String... args) {
         Args.notNull("args", args);
         CollectionUtils.addAll(this.args, (Object[]) args);
         return this;
      }

      public Builder withEnvironment(final Consumer<Map<String, Object>> envConfigurer) {
         if (env == null) {
            env = new TreeMap<>(SystemUtils.IS_OS_WINDOWS ? String.CASE_INSENSITIVE_ORDER : null);
            env.putAll(System.getenv());
         }
         envConfigurer.accept(env);
         return this;
      }

      public Builder withRedirectError(final Appendable target) {
         assertRedirectErrorToOuputNotConfigured();
         redirectError = target;
         return this;
      }

      public Builder withRedirectError(final Consumer<String> lineConsumer) {
         assertRedirectErrorToOuputNotConfigured();
         redirectError = lineConsumer;
         return this;
      }

      public Builder withRedirectError(final File target) {
         assertRedirectErrorToOuputNotConfigured();
         redirectError = target;
         return this;
      }

      public Builder withRedirectError(final OutputStream target) {
         assertRedirectErrorToOuputNotConfigured();
         redirectError = target;
         return this;
      }

      public Builder withRedirectError(final Path target) {
         return withRedirectError(target == null ? null : target.toFile());
      }

      public Builder withRedirectError(final PrintStream target) {
         return withRedirectError((OutputStream) target);
      }

      public Builder withRedirectErrorToOutput() {
         if (redirectError != null)
            throw new IllegalArgumentException("withRedirectErrorToOutput() and withRedirectError() are mutually exclusive.");
         return this;
      }

      public Builder withRedirectOutput(final Appendable target) {
         redirectOutput = target;
         return this;
      }

      public Builder withRedirectOutput(final Consumer<String> lineConsumer) {
         redirectOutput = lineConsumer;
         return this;
      }

      public Builder withRedirectOutput(final File target) {
         redirectOutput = target;
         return this;
      }

      public Builder withRedirectOutput(final OutputStream target) {
         redirectOutput = target;
         return this;
      }

      public Builder withRedirectOutput(final Path target) {
         redirectOutput = target == null ? null : target.toFile();
         return this;
      }

      public Builder withRedirectOutput(final PrintStream target) {
         return withRedirectOutput((OutputStream) target);
      }

      /**
       * Function to be used to convert non-String arguments and environment-variables to String objects
       */
      public Builder withStringifier(final Function<Object, String> stringifier) {
         this.stringifier = stringifier == null ? Objects::toString : stringifier;
         return this;
      }

      public Builder withWorkingDirectory(final File path) {
         Args.notNull("path", path);
         workDir = path;
         return this;
      }

      public Builder withWorkingDirectory(final Path path) {
         Args.notNull("path", path);
         workDir = path.toFile();
         return this;
      }
   }

   public enum ProcessState {
      /**
       * Process is still running
       */
      RUNNING,

      /**
       * Process finished execution and returned a 0 exit code
       */
      SUCCEEDED,

      /**
       * Process finished execution and returned a non-zero exit code
       */
      FAILED,

      /**
       * Process was programmatically stopped gracefully (SIGTERM).
       */
      TERMINATED,

      /**
       * Process was programmatically stopped forcefully (SIGKILL).
       */
      KILLED
   }

   /**
    * Wrapper around {@link Process} with convenience methods.
    */
   public static class ProcessWrapper {

      private final Process process;

      private volatile boolean isTerminateRequested;
      private volatile boolean isKillRequested;

      public ProcessWrapper(final Process process) {
         Args.notNull("process", process);
         this.process = process;
      }

      public int exitStatus() {
         return process.exitValue();
      }

      /**
       * The underlying process.
       */
      public Process getProcess() {
         return process;
      }

      public ProcessState getState() {
         if (process.isAlive())
            return ProcessState.RUNNING;
         if (isKillRequested)
            return ProcessState.KILLED;
         if (isTerminateRequested)
            return ProcessState.TERMINATED;
         return process.exitValue() == 0 ? ProcessState.SUCCEEDED : ProcessState.FAILED;
      }

      public InputStream getStdErr() {
         return process.getErrorStream();
      }

      public OutputStream getStdIn() {
         return process.getOutputStream();
      }

      public InputStream getStdOut() {
         return process.getInputStream();
      }

      public boolean isAlive() {
         return process.isAlive();
      }

      /**
       * Requests forceful termination of the process (SIGKILL) and immediately returns.
       */
      public ProcessWrapper kill() {
         if (process.isAlive()) {
            isKillRequested = true;
            process.destroyForcibly();
         }
         return this;
      }

      public CompletableFuture<ProcessWrapper> onExit() {
         return CompletableFuture.supplyAsync(() -> {
            try {
               process.waitFor();
            } catch (final InterruptedException ex) {
               Threads.handleInterruptedException(ex);
            }
            return this;
         }, BACKGROUND_THREADS);
      }

      public ProcessWrapper onExit(final Consumer<ProcessWrapper> action) {
         if (action == null)
            return this;

         BACKGROUND_THREADS.submit(() -> {
            try {
               process.waitFor();
               action.accept(this);
            } catch (final InterruptedException ex) {
               Threads.handleInterruptedException(ex);
            }
         });
         return this;
      }

      /**
       * Requests graceful termination of the process (SIGTERM) and immediately returns.
       */
      public ProcessWrapper terminate() {
         if (process.isAlive()) {
            isTerminateRequested = true;
            process.destroy();
         }
         return this;
      }

      /**
       * Requests graceful termination of the process (SIGTERM) and immediately returns.
       *
       * If timeout is reached and process is still running, requests forceful termination (SIGKILL).
       */
      public ProcessWrapper terminate(final int gracePeriod, final TimeUnit gracePeriodTimeUnit) {
         Args.notNegative("gracePeriod", gracePeriod);
         Args.notNull("gracePeriodTimeUnit", gracePeriodTimeUnit);

         if (process.isAlive()) {
            isTerminateRequested = true;
            process.destroy();
            BACKGROUND_THREADS.schedule(this::kill, gracePeriod, gracePeriodTimeUnit);
         }
         return this;
      }

      /**
       * Blocks until the process terminates.
       */
      public ProcessWrapper waitForExit() throws InterruptedException {
         process.waitFor();
         return this;
      }

      /**
       * Blocks until the process terminates or the timeout is reached.
       */
      public ProcessWrapper waitForExit(final int timeout, final TimeUnit timeUnit) throws InterruptedException {
         Args.notNegative("timeout", timeout);
         Args.notNull("timeUnit", timeUnit);

         process.waitFor(timeout, timeUnit);
         return this;
      }
   }

   private static final ScheduledThreadPoolExecutor BACKGROUND_THREADS = new ScheduledThreadPoolExecutor(0);

   static {
      BACKGROUND_THREADS.setKeepAliveTime(10, TimeUnit.SECONDS);
   }

   public static Builder builder(final File executable) {
      Args.notNull("executable", executable);
      return builder(executable.getPath());
   }

   public static Builder builder(final Path executable) {
      Args.notNull("executable", executable);
      return builder(executable.toString());
   }

   public static Builder builder(final String executable) {
      Args.notNull("executable", executable);
      return new Builder(executable);
   }

   /**
    * First requests graceful termination/shutdown of the process.
    * If grace period is over and the process is still running, requests forceful termination.
    * Blocks until process is destroyed.
    *
    * @return false if the given process was not alive, true if it was destroyed by this method.
    */
   public static boolean destroy(final Process process, final int gracePeriod, final TimeUnit gracePeriodTimeUnit)
      throws InterruptedException {
      Args.notNull("process", process);
      Args.notNegative("gracePeriod", gracePeriod);
      Args.notNull("gracePeriodTimeUnit", gracePeriodTimeUnit);

      if (!process.isAlive())
         return false;

      process.destroy();
      process.waitFor(gracePeriod, gracePeriodTimeUnit);
      if (process.isAlive()) {
         process.destroyForcibly();
         process.waitFor();
      }
      return true;
   }
}
