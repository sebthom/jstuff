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
package net.sf.jstuff.core.security.acl;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DelegatingSecurityManagerWithThreadLocalControl extends DelegatingSecurityManager {

    private Boolean isEnabledByDefault = true;

    /**
     * Determines if custom security checks implemented by this class are executed or if calls are only delegated to the underlying security manager
     */
    protected final ThreadLocal<Boolean> isEnabledForThread = new ThreadLocal<Boolean>() {
        @Override
        public Boolean get() {
            final Boolean enabled = super.get();
            if (enabled == null)
                return isEnabledByDefault;
            return enabled;
        };
    };

    public DelegatingSecurityManagerWithThreadLocalControl() {
        super();
    }

    public DelegatingSecurityManagerWithThreadLocalControl(final SecurityManager wrapped) {
        super(wrapped);
    }

    public boolean isEnabledByDefault() {
        return isEnabledByDefault;
    }

    public void setEnabledByDefault(final boolean enabledByDefault) {
        isEnabledByDefault = enabledByDefault;
    }

    public void setEnabledForCurrentThread(final boolean enabled) {
        isEnabledForThread.set(enabled);
    }
}
