/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.SystemUtils;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.io.MoreFiles;
import net.sf.jstuff.core.io.Processes;
import net.sf.jstuff.core.io.Processes.ProcessWrapper;
import net.sf.jstuff.core.logging.Logger;

/**
 * Allows programmatic execution of the KeyTool on Oracle and IBM JVMs.
 *
 * During execution this class tries to install a custom security manager to prevent JVM exist calls by the underlying KeyTool class implementation.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class KeyTool {

   private static final Logger LOG = Logger.create();

   public static Tuple2<X509Certificate, PrivateKey> createSelfSignedCertificate(final String subjectDN, final String keyAlgo,
      final int keySize, final int daysValid) throws GeneralSecurityException {

      final Path keyStoreFile = MoreFiles.getTempDirectory().resolve(UUID.randomUUID().toString() + ".jks");
      final @NonNull String[] args = { //
         "-genkey", //
         "-keyalg", keyAlgo, //
         "-alias", "selfsigned", //
         "-keystore", keyStoreFile.toString(), //
         "-storepass", "changeit", //
         "-keypass", "changeit", //
         "-dname", subjectDN, //
         "-validity", Integer.toString(daysValid), //
         "-keysize", Integer.toString(keySize) //
      };
      try {
         run(args);
      } catch (final Exception ex) {
         throw new GeneralSecurityException(ex);
      }

      try (var keyStoreIS = Files.newInputStream(keyStoreFile, StandardOpenOption.READ)) {
         final var keyStore = KeyStore.getInstance("JKS");
         keyStore.load(keyStoreIS, "changeit".toCharArray());
         return Tuple2.create( //
            (X509Certificate) asNonNull(keyStore.getCertificate("selfsigned")), //
            (PrivateKey) asNonNull(keyStore.getKey("selfsigned", "changeit".toCharArray())) //
         );
      } catch (final IOException ex) {
         throw new GeneralSecurityException(ex);
      } finally {
         MoreFiles.forceDeleteNowOrOnExit(keyStoreFile);
      }
   }

   public static void main(final @NonNull String[] args) throws IOException { // CHECKSTYLE:IGNORE UncommentedMain
      try {
         final ProcessWrapper prc = Processes.builder(SystemUtils.getJavaHome().toPath().resolve("bin/keytool")).withArgs(args)
            .withRedirectOutput(System.out) //
            .withRedirectError(System.err) //
            .start() //
            .waitForExit(10, TimeUnit.SECONDS) //
            .terminate(5, TimeUnit.SECONDS);

         System.exit(prc.exitStatus());
      } catch (final InterruptedException ex) {
         Threads.handleInterruptedException(ex);
         throw new IOException(ex);
      }
   }

   public static String run(final @NonNull String... args) throws IOException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Executing keytool with \"%s\"...", Strings.join(args, "\", \""));
      }

      try {
         final var out = new StringBuilder();
         final ProcessWrapper prc = Processes.builder(SystemUtils.getJavaHome().toPath().resolve("bin/keytool")).withArgs(args)
            .withRedirectErrorToOutput() //
            .withRedirectOutput(out) //
            .start() //
            .waitForExit(10, TimeUnit.SECONDS) //
            .terminate(5, TimeUnit.SECONDS);

         if (prc.exitStatus() != 0)
            throw new IOException("Key tool failed with: " + out);
         return out.toString();
      } catch (final InterruptedException ex) {
         Threads.handleInterruptedException(ex);
         throw new IOException(ex);
      }
   }
}
