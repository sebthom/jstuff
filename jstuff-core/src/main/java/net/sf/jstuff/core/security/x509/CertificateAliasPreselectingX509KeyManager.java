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

import javax.net.ssl.X509KeyManager;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CertificateAliasPreselectingX509KeyManager extends DelegatingX509KeyManager {

    private final String clientCertAlias;
    private final String serverCertAlias;

    public CertificateAliasPreselectingX509KeyManager(final X509KeyManager wrapped, final String clientCertAlias, final String serverCertAlias) {
        super(wrapped);
        this.clientCertAlias = clientCertAlias;
        this.serverCertAlias = serverCertAlias;
    }

    @Override
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        return clientCertAlias == null ? super.chooseClientAlias(keyType, issuers, socket) : clientCertAlias;
    }

    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return serverCertAlias == null ? super.chooseServerAlias(keyType, issuers, socket) : serverCertAlias;
    }
}
