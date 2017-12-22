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
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.X509KeyManager;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeX509KeyManager implements X509KeyManager {
    private final List<X509KeyManager> keyManagers;

    public CompositeX509KeyManager(final Collection<X509KeyManager> keyManagers) {
        Args.notNull("keyManagers", keyManagers);
        Args.noNulls("keyManagers", keyManagers);

        this.keyManagers = new ArrayList<X509KeyManager>(keyManagers);
    }

    public CompositeX509KeyManager(final X509KeyManager... keyManagers) {
        Args.notNull("keyManagers", keyManagers);
        Args.noNulls("keyManagers", keyManagers);

        this.keyManagers = Arrays.asList(keyManagers);
    }

    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        for (final X509KeyManager keyManager : keyManagers) {
            final String alias = keyManager.chooseClientAlias(keyType, issuers, socket);
            if (alias != null)
                return alias;
        }
        return null;
    }

    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        for (final X509KeyManager keyManager : keyManagers) {
            final String alias = keyManager.chooseServerAlias(keyType, issuers, socket);
            if (alias != null)
                return alias;
        }
        return null;
    }

    public X509Certificate[] getCertificateChain(final String alias) {
        for (final X509KeyManager keyManager : keyManagers) {
            final X509Certificate[] chain = keyManager.getCertificateChain(alias);
            if (chain != null && chain.length > 0)
                return chain;
        }
        return null;
    }

    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        final List<String> result = new ArrayList<String>();
        for (final X509KeyManager keyManager : keyManagers) {
            CollectionUtils.addAll(result, keyManager.getClientAliases(keyType, issuers));
        }
        if (result.size() == 0)
            return null;
        return result.toArray(new String[result.size()]);
    }

    public PrivateKey getPrivateKey(final String alias) {
        for (final X509KeyManager keyManager : keyManagers) {
            final PrivateKey privateKey = keyManager.getPrivateKey(alias);
            if (privateKey != null)
                return privateKey;
        }
        return null;
    }

    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        final List<String> result = new ArrayList<String>();
        for (final X509KeyManager keyManager : keyManagers) {
            CollectionUtils.addAll(result, keyManager.getServerAliases(keyType, issuers));
        }
        if (result.size() == 0)
            return null;
        return result.toArray(new String[result.size()]);
    }
}
