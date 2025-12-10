/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.concurrent.ScalingScheduledExecutorService;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.functional.ThrowingConsumer;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Processes {

   public static class Builder {

      private Function<Object, String> stringifier = Objects::toString;

      private final String executable;
      private final List<Object> args = new ArrayList<>(2);
      private @Nullable Map<String, Object> env;
      private @Nullable Object /*CharSequence|File|InputStream*/ input;
      private @Nullable Consumer<ProcessWrapper> onExit;
      private @Nullable File workDir;

      private boolean inheritStdIn;
      private boolean inheritStdOut;
      private boolean inheritStdErr;

      private boolean redirectErrorToOutput;
      private @Nullable Object /*Appendable|File|OutputStream|Consumer<String>*/ redirectError;
      private @Nullable Object /*Appendable|File|OutputStream|Consumer<String>*/ redirectOutput;

      protected Builder(final String exe) {
         executable = exe;
      }

      private void assertRedirectErrorToOuputNotConfigured() {
         if (redirectErrorToOutput)
            throw new IllegalStateException("withRedirectError() and withRedirectErrorToOutput() are mutually exclusive.");
         assertStdErrNotInherited();
      }

      private void assertStdErrNotInherited() {
         if (inheritStdErr)
            throw new IllegalStateException("withRedirectError()/withRedirectErrorToOutput() cannot be combined with withInheritStdErr().");
      }

      private void assertStdInNotInherited() {
         if (inheritStdIn)
            throw new IllegalStateException("withInput() cannot be combined with withInheritStdIn().");
      }

      private void assertStdOutNotInherited() {
         if (inheritStdOut)
            throw new IllegalStateException("withRedirectOutput() cannot be combined with withInheritStdOut().");
      }

      public Builder onExit(final @Nullable Consumer<ProcessWrapper> action) {
         onExit = action;
         return this;
      }

      public Builder onExit(final @Nullable ThrowingConsumer<ProcessWrapper, Throwable> action) {
         onExit = action;
         return this;
      }

      private CompletableFuture<@Nullable Void> redirect(final InputStream in, final Appendable out) {
         return CompletableFuture.runAsync(() -> {
            try {
               IOUtils.copy(new InputStreamReader(in, StandardCharsets.UTF_8), out);
            } catch (final IOException ex) {
               throw new RuntimeIOException(ex);
            }
         }, BACKGROUND_THREADS);
      }

      @SuppressWarnings("null")
      private CompletableFuture<@Nullable Void> redirect(final InputStream in, final Consumer<String> lineConsumer) {
         return CompletableFuture.runAsync(() -> {
            try (var sc = new Scanner(in)) {
               while (sc.hasNextLine()) {
                  lineConsumer.accept(sc.nextLine());
               }
            }
         }, BACKGROUND_THREADS);
      }

      private CompletableFuture<@Nullable Void> redirect(final InputStream in, final OutputStream out) {
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

         LOG.debug("Executing command: " + command);
         final var pb = new ProcessBuilder(command);
         final var env = this.env;
         if (env != null) {
            final Map<String, String> pbEnv = pb.environment();
            pbEnv.clear();
            env.forEach((k, v) -> pbEnv.put(k, stringifier.apply(v)));
         }

         if (workDir != null) {
            pb.directory(workDir);
         }

         /*
          * Configure ProcessBuilder redirects that must be set before start().
          */
         if (inheritStdIn) {
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
         } else if (input instanceof final File f) {
            pb.redirectInput(f);
         }

         if (redirectErrorToOutput) {
            pb.redirectErrorStream(true);
         } else if (inheritStdErr) {
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
         } else if (redirectError instanceof final File f) {
            pb.redirectError(f);
         }

         if (inheritStdOut) {
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
         } else if (redirectOutput instanceof final File f) {
            pb.redirectOutput(f);
         }

         final Process proc = pb.start();

         /*
          * Handle stdin/output redirection that uses the process streams.
          */
         if (!inheritStdIn && input != null && !(input instanceof File)) {
            final var input = this.input;
            if (input instanceof final InputStream is) {
               writeToStdIn(is, proc);
            } else if (input instanceof final CharSequence cs) {
               writeToStdIn(cs, proc);
            }
         }

         if (!inheritStdErr && !redirectErrorToOutput && redirectError != null && !(redirectError instanceof File)) {
            final var redirectError = this.redirectError;
            if (redirectError instanceof final OutputStream os) {
               redirect(proc.getErrorStream(), os);
            } else if (redirectError instanceof final Appendable appendable) {
               redirect(proc.getErrorStream(), appendable);
            } else {
               redirect(proc.getErrorStream(), (Consumer<String>) redirectError);
            }
         }

         if (!inheritStdOut && redirectOutput != null && !(redirectOutput instanceof File)) {
            final var redirectOutput = this.redirectOutput;
            if (redirectOutput instanceof final OutputStream os) {
               redirect(proc.getInputStream(), os);
            } else if (redirectOutput instanceof final Appendable appendable) {
               redirect(proc.getInputStream(), appendable);
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
         args.add(arg.toString());
         return this;
      }

      @SuppressWarnings("null")
      public Builder withArgs(final List<?> args) {
         Args.notNull("args", args);
         this.args.addAll(args);
         return this;
      }

      public Builder withArgs(final @NonNull Object... args) {
         Args.notNull("args", args);
         Collections.addAll(this.args, args);
         return this;
      }

      public Builder withArgs(final @NonNull String... args) {
         Args.notNull("args", args);
         CollectionUtils.addAll(this.args, (@NonNull Object[]) args);
         return this;
      }

      public Builder withEnvironment(final Consumer<Map<String, Object>> envConfigurer) {
         var env = this.env;
         if (env == null) {
            env = this.env = new TreeMap<>(SystemUtils.IS_OS_WINDOWS ? String.CASE_INSENSITIVE_ORDER : null);
            env.putAll(System.getenv());
         }
         envConfigurer.accept(env);
         return this;
      }

      /**
       * Inherit stderr of the current process.
       *
       * <p>
       * Cannot be combined with {@code withRedirectError()} or {@code withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withInheritError() {
         if (redirectError != null || redirectErrorToOutput)
            throw new IllegalStateException(
               "withInheritStdErr() cannot be combined with withRedirectError() or withRedirectErrorToOutput().");
         inheritStdErr = true;
         return this;
      }

      /**
       * Inherit stdin of the current process.
       *
       * <p>
       * Cannot be combined with {@code withInput()}.
       * </p>
       */
      public Builder withInheritInput() {
         if (input != null)
            throw new IllegalStateException("withInheritStdIn() cannot be combined with withInput().");
         inheritStdIn = true;
         return this;
      }

      /**
       * Inherits stdin, stdout and stderr of the current process.
       *
       * <p>
       * Cannot be combined with {@code withInput()}, {@code withRedirectOutput()}, {@code withRedirectError()}
       * or {@code withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withInheritIO() {
         if (input != null)
            throw new IllegalStateException("withInheritStdIn() cannot be combined with withInput().");
         if (redirectOutput != null)
            throw new IllegalStateException("withInheritStdOut() cannot be combined with withRedirectOutput().");
         if (redirectError != null || redirectErrorToOutput)
            throw new IllegalStateException(
               "withInheritStdErr() cannot be combined with withRedirectError() or withRedirectErrorToOutput().");

         inheritStdIn = true;
         inheritStdOut = true;
         inheritStdErr = true;
         return this;
      }

      /**
       * Inherit stdout of the current process.
       *
       * <p>
       * Cannot be combined with {@code withRedirectOutput()}.
       * </p>
       */
      public Builder withInheritOutput() {
         if (redirectOutput != null)
            throw new IllegalStateException("withInheritStdOut() cannot be combined with withRedirectOutput().");
         inheritStdOut = true;
         return this;
      }

      /**
       * Configures stdin content for the process from an input stream.
       *
       * <p>
       * Cannot be combined with {@link #withInheritInput()} which inherits stdin from the current process.
       * </p>
       */
      @SuppressWarnings("resource")
      public Builder withInput(final byte @Nullable [] input) {
         if (input != null) {
            assertStdInNotInherited();
         }
         this.input = input == null ? null : new FastByteArrayInputStream(input);
         return this;
      }

      /**
       * Configures stdin content for the process.
       *
       * <p>
       * Cannot be combined with {@link #withInheritInput()} which inherits stdin from the current process.
       * </p>
       */
      public Builder withInput(final @Nullable CharSequence input) {
         if (input != null) {
            assertStdInNotInherited();
         }
         this.input = input;
         return this;
      }

      /**
       * Configures stdin content for the process.
       *
       * <p>
       * Cannot be combined with {@link #withInheritInput()} which inherits stdin from the current process.
       * </p>
       */
      public Builder withInput(final @Nullable File input) {
         if (input != null) {
            assertStdInNotInherited();
         }
         this.input = input;
         return this;
      }

      /**
       * Configures stdin content for the process from an input stream.
       *
       * <p>
       * Cannot be combined with {@link #withInheritInput()} which inherits stdin from the current process.
       * </p>
       */
      public Builder withInput(final @Nullable InputStream input) {
         if (input != null) {
            assertStdInNotInherited();
         }
         this.input = input;
         return this;
      }

      /**
       * Configures stdin content for the process.
       *
       * <p>
       * Cannot be combined with {@link #withInheritInput()} which inherits stdin from the current process.
       * </p>
       */
      public Builder withInput(final @Nullable Path input) {
         if (input != null) {
            assertStdInNotInherited();
         }
         this.input = input == null ? null : input.toFile();
         return this;
      }

      /**
       * Redirects stderr to the given {@link Appendable}.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or {@link #withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withRedirectError(final @Nullable Appendable target) {
         if (target != null) {
            assertRedirectErrorToOuputNotConfigured();
         }
         redirectError = target;
         return this;
      }

      /**
       * Redirects stderr line by line to the given consumer.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or {@link #withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withRedirectError(final @Nullable Consumer<String> lineConsumer) {
         if (lineConsumer != null) {
            assertRedirectErrorToOuputNotConfigured();
         }
         redirectError = lineConsumer;
         return this;
      }

      /**
       * Redirects stderr to the given file.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or {@link #withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withRedirectError(final @Nullable File target) {
         if (target != null) {
            assertRedirectErrorToOuputNotConfigured();
         }
         redirectError = target;
         return this;
      }

      /**
       * Redirects stderr to the given output stream.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or {@link #withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withRedirectError(final @Nullable OutputStream target) {
         if (target != null) {
            assertRedirectErrorToOuputNotConfigured();
         }
         redirectError = target;
         return this;
      }

      /**
       * Redirects stderr to the given file.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or {@link #withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withRedirectError(final @Nullable Path target) {
         return withRedirectError(target == null ? null : target.toFile());
      }

      /**
       * Redirects stderr to the given print stream.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or {@link #withRedirectErrorToOutput()}.
       * </p>
       */
      public Builder withRedirectError(final @Nullable PrintStream target) {
         return withRedirectError((OutputStream) target);
      }

      /**
       * Redirects stderr to stdout.
       *
       * <p>
       * Cannot be combined with {@link #withInheritError()} or <code>#withRedirectError(...)</code>.
       * </p>
       */
      public Builder withRedirectErrorToOutput() {
         if (redirectError != null)
            throw new IllegalArgumentException("withRedirectErrorToOutput() and withRedirectError() are mutually exclusive.");
         assertStdErrNotInherited();
         redirectErrorToOutput = true;
         return this;
      }

      /**
       * Redirects stdout to the given {@link Appendable}.
       *
       * <p>
       * Cannot be combined with {@link #withInheritOutput()}.
       * </p>
       */
      public Builder withRedirectOutput(final @Nullable Appendable target) {
         if (target != null) {
            assertStdOutNotInherited();
         }
         redirectOutput = target;
         return this;
      }

      /**
       * Redirects stdout line by line to the given consumer.
       *
       * <p>
       * Cannot be combined with {@link #withInheritOutput()}.
       * </p>
       */
      public Builder withRedirectOutput(final @Nullable Consumer<String> lineConsumer) {
         if (lineConsumer != null) {
            assertStdOutNotInherited();
         }
         redirectOutput = lineConsumer;
         return this;
      }

      /**
       * Redirects stdout to the given file.
       *
       * <p>
       * Cannot be combined with {@link #withInheritOutput()}.
       * </p>
       */
      public Builder withRedirectOutput(final @Nullable File target) {
         if (target != null) {
            assertStdOutNotInherited();
         }
         redirectOutput = target;
         return this;
      }

      /**
       * Redirects stdout to the given output stream.
       *
       * <p>
       * Cannot be combined with {@link #withInheritOutput()}.
       * </p>
       */
      public Builder withRedirectOutput(final @Nullable OutputStream target) {
         if (target != null) {
            assertStdOutNotInherited();
         }
         redirectOutput = target;
         return this;
      }

      /**
       * Redirects stdout to the given file.
       *
       * <p>
       * Cannot be combined with {@link #withInheritOutput()}.
       * </p>
       */
      public Builder withRedirectOutput(final @Nullable Path target) {
         if (target != null) {
            assertStdOutNotInherited();
         }
         redirectOutput = target == null ? null : target.toFile();
         return this;
      }

      /**
       * Redirects stdout to the given print stream.
       *
       * <p>
       * Cannot be combined with {@link #withInheritOutput()}.
       * </p>
       */
      public Builder withRedirectOutput(final @Nullable PrintStream target) {
         return withRedirectOutput((OutputStream) target);
      }

      /**
       * Function to be used to convert non-String arguments and environment-variables to String objects
       */
      public Builder withStringifier(final @Nullable Function<Object, String> stringifier) {
         this.stringifier = stringifier == null ? Objects::toString : stringifier;
         return this;
      }

      public Builder withWorkingDirectory(final @Nullable File path) {
         if (path == null) {
            workDir = null;
         } else {
            Args.isDirectoryReadable("path", path.toPath());
            workDir = path;
         }
         return this;
      }

      public Builder withWorkingDirectory(final @Nullable Path path) {
         if (path == null) {
            workDir = null;
         } else {
            Args.isDirectoryReadable("path", path);
            workDir = path.toFile();
         }
         return this;
      }

      private CompletableFuture<@Nullable Void> writeToStdIn(final CharSequence in, final Process proc) {
         return CompletableFuture.runAsync(() -> {
            try (var out = proc.getOutputStream()) {
               IOUtils.write(in, out, StandardCharsets.UTF_8);
            } catch (final IOException ex) {
               throw new RuntimeIOException(ex);
            }
         }, BACKGROUND_THREADS);
      }

      private CompletableFuture<@Nullable Void> writeToStdIn(final InputStream in, final Process proc) {
         return CompletableFuture.runAsync(() -> {
            try (var out = proc.getOutputStream()) {
               IOUtils.copy(in, out);
            } catch (final IOException ex) {
               throw new RuntimeIOException(ex);
            } finally {
               IOUtils.closeQuietly(in);
            }
         }, BACKGROUND_THREADS);
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

      public ProcessWrapper onExit(final @Nullable Consumer<ProcessWrapper> action) {
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

      public ProcessWrapper onExit(final @Nullable ThrowingConsumer<ProcessWrapper, Throwable> action) {
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

   private static final Logger LOG = Logger.create();

   private static final ScheduledExecutorService BACKGROUND_THREADS = new ScalingScheduledExecutorService(0, Integer.MAX_VALUE, Duration
      .ofSeconds(30));

   public static Builder builder(final File executable) {
      return builder(executable.getPath());
   }

   public static Builder builder(final Path executable) {
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
   public static boolean destroy(final Process process, final Duration gracePeriod) throws InterruptedException {
      return destroy(process, gracePeriod.toMillis(), TimeUnit.MILLISECONDS);
   }

   /**
    * First requests graceful termination/shutdown of the process.
    * If grace period is over and the process is still running, requests forceful termination.
    * Blocks until process is destroyed.
    *
    * @return false if the given process was not alive, true if it was destroyed by this method.
    */
   @SuppressWarnings("resource")
   public static boolean destroy(final Process process, final long gracePeriod, final TimeUnit gracePeriodTimeUnit)
         throws InterruptedException {
      Args.notNegative("gracePeriod", gracePeriod);
      Args.notNull("gracePeriodTimeUnit", gracePeriodTimeUnit);

      boolean forcibly = false;

      try {
         if (!process.isAlive())
            return false;

         process.destroy();
         if (!process.waitFor(gracePeriod, gracePeriodTimeUnit)) {
            process.destroyForcibly();
            forcibly = true;
            process.waitFor();
         }
      } catch (final InterruptedException ex) {
         process.destroyForcibly();
         forcibly = true;
         process.waitFor();
         throw ex;
      } finally {
         // also try to close any dangling streams in case the process is not alive anymore
         IOUtils.closeQuietly(process.getInputStream());
         IOUtils.closeQuietly(process.getOutputStream());
         IOUtils.closeQuietly(process.getErrorStream());
      }

      LOG.debug("Process %s terminated (%s)", process.pid(), forcibly ? "forcefully" : "gracefully");
      return true;
   }
}
