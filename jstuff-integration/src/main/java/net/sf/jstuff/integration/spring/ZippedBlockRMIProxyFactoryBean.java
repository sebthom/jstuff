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
package net.sf.jstuff.integration.spring;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockRMIProxyFactoryBean extends RmiProxyFactoryBean {
    private boolean useCompressedRMISocketFactory = true;

    public ZippedBlockRMIProxyFactoryBean() {
        setLookupStubOnStartup(false);
        setRefreshStubOnConnectFailure(true);
    }

    @Override
    public void afterPropertiesSet() {
        if (useCompressedRMISocketFactory)
            setRegistryClientSocketFactory(new ZippedBlockRMISocketFactory());

        super.afterPropertiesSet();
    }

    public boolean isUseCompressedRMISocketFactory() {
        return useCompressedRMISocketFactory;
    }

    public void setUseCompressedRMISocketFactory(final boolean useCompressedRMISocketFactory) {
        this.useCompressedRMISocketFactory = useCompressedRMISocketFactory;
    }
}
