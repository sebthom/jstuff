/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.core.security.x509;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeX509TrustManager implements X509TrustManager {
    private static final Logger LOG = Logger.create();

    private final List<X509TrustManager> trustManagers;

    public CompositeX509TrustManager(final List<X509TrustManager> trustManagers) {
        Args.notNull("keyManagers", trustManagers);
        Args.noNulls("trustManagers", trustManagers);

        this.trustManagers = new ArrayList<X509TrustManager>(trustManagers);
    }

    public CompositeX509TrustManager(final X509TrustManager... trustManagers) {
        Args.notNull("keyManagers", trustManagers);
        Args.noNulls("trustManagers", trustManagers);

        this.trustManagers = Arrays.asList(trustManagers);
    }

    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        for (final X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkClientTrusted(chain, authType);
                return;
            } catch (final CertificateException ex) {
                LOG.debug(ex);
            }
        }
        throw new CertificateException("Client certificate chain not trusted by any registered trust manager");
    }

    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        for (final X509TrustManager trustManager : trustManagers) {
            try {
                trustManager.checkServerTrusted(chain, authType);
                return;
            } catch (final CertificateException ex) {
                LOG.debug(ex);
            }
        }
        throw new CertificateException("Server certificate chain not trusted by any registered trust manager");
    }

    public X509Certificate[] getAcceptedIssuers() {
        final List<X509Certificate> result = new ArrayList<X509Certificate>();
        for (final X509TrustManager trustManager : trustManagers) {
            CollectionUtils.addAll(result, trustManager.getAcceptedIssuers());
        }
        return result.toArray(new X509Certificate[result.size()]);
    }
}