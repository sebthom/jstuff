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
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingX509KeyManager implements X509KeyManager {

    private final X509KeyManager wrapped;

    public DelegatingX509KeyManager(final X509KeyManager wrapped) {
        this.wrapped = wrapped;
    }

    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        return wrapped.chooseClientAlias(keyType, issuers, socket);
    }

    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return wrapped.chooseServerAlias(keyType, issuers, socket);
    }

    public X509Certificate[] getCertificateChain(final String alias) {
        return wrapped.getCertificateChain(alias);
    }

    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return wrapped.getClientAliases(keyType, issuers);
    }

    public PrivateKey getPrivateKey(final String alias) {
        return wrapped.getPrivateKey(alias);
    }

    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return wrapped.getServerAliases(keyType, issuers);
    }
}