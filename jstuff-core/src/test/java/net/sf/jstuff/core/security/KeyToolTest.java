/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.security;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.tuple.Tuple2;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class KeyToolTest extends TestCase {

    public void testCreateSelfSignedCertificate() throws GeneralSecurityException {

        // illegal subject DN
        try {
            KeyTool.createSelfSignedCertificate("sdfdsf", "RSA", 512, 14);
            fail();
        } catch (final Exception ex) {
            assertEquals(IllegalArgumentException.class, ex.getClass());
        }

        // illegal key size
        try {
            KeyTool.createSelfSignedCertificate("CN=foo", "RSA", 20, 14);
            fail();
        } catch (final Exception ex) {
            assertEquals(IllegalArgumentException.class, ex.getClass());
        }

        // illegal algorithm
        try {
            KeyTool.createSelfSignedCertificate("CN=foo", "BLABLA", 512, 14);
            fail();
        } catch (final Exception ex) {
            assertEquals(IllegalArgumentException.class, ex.getClass());
        }

        // illegal validity
        try {
            KeyTool.createSelfSignedCertificate("CN=foo", "RSA", 512, -1);
            fail();
        } catch (final Exception ex) {
            assertEquals(IllegalArgumentException.class, ex.getClass());
        }

        final Tuple2<X509Certificate, PrivateKey> result = KeyTool.createSelfSignedCertificate("CN=foo", "RSA", 512, 14);

        assertEquals("CN=foo", result.get1().getSubjectDN().getName());
        assertEquals("X.509", result.get1().getType());
        result.get1().checkValidity();

        assertEquals("RSA", result.get1().getPublicKey().getAlgorithm());
        assertEquals("X.509", result.get1().getPublicKey().getFormat());

        assertEquals("RSA", result.get2().getAlgorithm());
        assertEquals("PKCS#8", result.get2().getFormat());

    }
}
