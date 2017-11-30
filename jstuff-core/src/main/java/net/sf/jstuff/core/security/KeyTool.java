/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.UUID;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.exception.Exceptions;
import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.reflection.exception.InvokingMethodFailedException;
import net.sf.jstuff.core.security.acl.NoExitSecurityManager;
import net.sf.jstuff.core.security.acl.NoExitSecurityManager.ExitNotAllowedException;
import net.sf.jstuff.core.validation.Assert;

/**
 * Allows programmatic execution of the KeyTool on Sun/Oracle and IBM JVMs.
 *
 * During execution this class tries to install a custom security manager to prevent JVM exist calls by the underlying KeyTool class implementation.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class KeyTool {

    private static final Logger LOG = Logger.create();

    private static Class<?> keyToolClass;
    private static Method keyToolRunMethod;

    private static final NoExitSecurityManager SEC_MAN = new NoExitSecurityManager();

    static {
        keyToolClass = Types.find("sun.security.tools.KeyTool"); // Oracle JDK <= 7
        if (keyToolClass == null) {
            keyToolClass = Types.find("sun.security.tools.keytool.Main"); // Oracle JDK 8+
        }
        if (keyToolClass == null) {
            keyToolClass = Types.find("com.ibm.crypto.tools.KeyTool");
        }

        if (keyToolClass != null) {
            keyToolRunMethod = Methods.findAny(keyToolClass, "run", String[].class, PrintStream.class);
            if (keyToolRunMethod == null) {
                keyToolRunMethod = Methods.findAny(keyToolClass, "a", String[].class, PrintStream.class); // obfuscated name on IBM JDK
            }
        }

        SEC_MAN.setEnabledByDefault(false);
    }

    public static Tuple2<X509Certificate, PrivateKey> createSelfSignedCertificate(final String subjectDN, final String keyAlgo, final int keySize,
            final int daysValid) throws GeneralSecurityException, IllegalArgumentException {
        File keyStoreFile = null;
        InputStream keyStoreIS = null;
        try {
            keyStoreFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString() + ".jks");
            final String[] args = new String[] { "-genkey", //
                    "-keyalg", keyAlgo, //
                    "-alias", "selfsigned", //
                    "-keystore", keyStoreFile.getPath(), //
                    "-storepass", "changeit", //
                    "-keypass", "changeit", //
                    "-dname", subjectDN, //
                    "-validity", Integer.toString(daysValid), //
                    "-keysize", Integer.toString(keySize) //
            };
            run(args);

            keyStoreIS = new FileInputStream(keyStoreFile);
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keyStoreIS, "changeit".toCharArray());
            return Tuple2.create( //
                (X509Certificate) keyStore.getCertificate("selfsigned"), //
                (PrivateKey) keyStore.getKey("selfsigned", "changeit".toCharArray()) //
            );
        } catch (final IOException ex) {
            throw new GeneralSecurityException(ex);
        } finally {
            IOUtils.closeQuietly(keyStoreIS);
            if (keyStoreFile != null && keyStoreFile.exists()) {
                keyStoreFile.delete();
                keyStoreFile.deleteOnExit();
            }
        }
    }

    private static void installNoExitSecurityManager() {
        final SecurityManager current = System.getSecurityManager();

        SEC_MAN.setEnabledForCurrentThread(true);

        // if no security manager is installed, install our
        if (current == null) {
            SEC_MAN.install();
            return;
        }

        // check if our thread local security manager already is at the top of the food chain
        if (SEC_MAN.isInstalled())
            return;

        // if any security manager exists, check if it already prevents System.exit calls
        try {
            current.checkExit(1);
        } catch (final SecurityException ex) {
            return;
        }

        // replace the current security manager with our
        SEC_MAN.install();
    }

    public static void main(final String[] args) {
        try {
            System.out.println(run(args));
        } catch (final IllegalArgumentException ex) {
            System.exit(1);
        }
    }

    @SuppressWarnings("resource")
    public static String run(final String... args) throws IllegalArgumentException {
        Assert.notNull(keyToolClass, "KeyTool class not found!");
        Assert.notNull(keyToolRunMethod, "KeyTool run method not found!");

        final Object kt = Types.newInstance(keyToolClass);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing %s.run(\"%s\")...", keyToolClass.getName(), Strings.join(args, "\", \""));
        }

        synchronized (SEC_MAN) {
            try {
                installNoExitSecurityManager();
                final FastByteArrayOutputStream result = new FastByteArrayOutputStream();
                Methods.invoke(kt, keyToolRunMethod, args, new PrintStream(result));
                return result.toString();
            } catch (final InvokingMethodFailedException ex) {
                if (Exceptions.getCauseOfType(ex, ExitNotAllowedException.class) != null)
                    throw new IllegalArgumentException("An unexpected error occured while processing input arguments.");
                throw ex;
            } finally {
                SEC_MAN.setEnabledForCurrentThread(false);
                SEC_MAN.uninstall();
            }
        }
    }
}
