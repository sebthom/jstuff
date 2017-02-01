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
package net.sf.jstuff.core.security.acl;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * Security manager that delegates all method invocations to the wrapped security manager instance.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingSecurityManager extends NoOpSecurityManager {

    private SecurityManager wrapped;

    public DelegatingSecurityManager() {
        this(null);
    }

    public DelegatingSecurityManager(final SecurityManager wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void checkAccept(final String host, final int port) {
        if (wrapped == null) {
            super.checkAccept(host, port);
        } else {
            wrapped.checkAccept(host, port);
        }
    }

    @Override
    public void checkAccess(final Thread t) {
        if (wrapped == null) {
            super.checkAccess(t);
        } else {
            wrapped.checkAccess(t);
        }
    }

    @Override
    public void checkAccess(final ThreadGroup g) {
        if (wrapped == null) {
            super.checkAccess(g);
        } else {
            wrapped.checkAccess(g);
        }
    }

    @Override
    public void checkAwtEventQueueAccess() {
        if (wrapped == null) {
            super.checkAwtEventQueueAccess();
        } else {
            wrapped.checkAwtEventQueueAccess();
        }
    }

    @Override
    public void checkConnect(final String host, final int port) {
        if (wrapped == null) {
            super.checkConnect(host, port);
        } else {
            wrapped.checkConnect(host, port);
        }
    }

    @Override
    public void checkConnect(final String host, final int port, final Object context) {
        if (wrapped == null) {
            super.checkConnect(host, port, context);
        } else {
            wrapped.checkConnect(host, port, context);
        }
    }

    @Override
    public void checkCreateClassLoader() {
        if (wrapped == null) {
            super.checkCreateClassLoader();
        } else {
            wrapped.checkCreateClassLoader();
        }
    }

    @Override
    public void checkDelete(final String file) {
        if (wrapped == null) {
            super.checkDelete(file);
        } else {
            wrapped.checkDelete(file);
        }
    }

    @Override
    public void checkExec(final String cmd) {
        if (wrapped == null) {
            super.checkExec(cmd);
        } else {
            wrapped.checkExec(cmd);
        }
    }

    @Override
    public void checkExit(final int status) {
        if (wrapped == null) {
            super.checkExit(status);
        } else {
            wrapped.checkExit(status);
        }
    }

    @Override
    public void checkLink(final String lib) {
        if (wrapped == null) {
            super.checkLink(lib);
        } else {
            wrapped.checkLink(lib);
        }
    }

    @Override
    public void checkListen(final int port) {
        if (wrapped == null) {
            super.checkListen(port);
        } else {
            wrapped.checkListen(port);
        }
    }

    @Override
    public void checkMemberAccess(final Class<?> clazz, final int which) {
        if (wrapped == null) {
            super.checkMemberAccess(clazz, which);
        } else {
            wrapped.checkMemberAccess(clazz, which);
        }
    }

    @Override
    public void checkMulticast(final InetAddress maddr) {
        if (wrapped == null) {
            super.checkMulticast(maddr);
        } else {
            wrapped.checkMulticast(maddr);
        }
    }

    @Deprecated
    @Override
    public void checkMulticast(final InetAddress maddr, final byte ttl) {
        if (wrapped == null) {
            super.checkMulticast(maddr, ttl);
        } else {
            wrapped.checkMulticast(maddr, ttl);
        }
    }

    @Override
    public void checkPackageAccess(final String pkg) {
        if (wrapped == null) {
            super.checkPackageAccess(pkg);
        } else {
            wrapped.checkPackageAccess(pkg);
        }
    }

    @Override
    public void checkPackageDefinition(final String pkg) {
        if (wrapped == null) {
            super.checkPackageDefinition(pkg);
        } else {
            wrapped.checkPackageDefinition(pkg);
        }
    }

    @Override
    public void checkPermission(final Permission perm) {
        if (wrapped == null) {
            super.checkPermission(perm);
        } else {
            wrapped.checkPermission(perm);
        }
    }

    @Override
    public void checkPermission(final Permission perm, final Object context) {
        if (wrapped == null) {
            super.checkPermission(perm, context);
        } else {
            wrapped.checkPermission(perm, context);
        }
    }

    @Override
    public void checkPrintJobAccess() {
        if (wrapped == null) {
            super.checkPrintJobAccess();
        } else {
            wrapped.checkPrintJobAccess();
        }
    }

    @Override
    public void checkPropertiesAccess() {
        if (wrapped == null) {
            super.checkPropertiesAccess();
        } else {
            wrapped.checkPropertiesAccess();
        }
    }

    @Override
    public void checkPropertyAccess(final String key) {
        if (wrapped == null) {
            super.checkPropertyAccess(key);
        } else {
            wrapped.checkPropertyAccess(key);
        }
    }

    @Override
    public void checkRead(final FileDescriptor fd) {
        if (wrapped == null) {
            super.checkRead(fd);
        } else {
            wrapped.checkRead(fd);
        }
    }

    @Override
    public void checkRead(final String file) {
        if (wrapped == null) {
            super.checkRead(file);
        } else {
            wrapped.checkRead(file);
        }
    }

    @Override
    public void checkRead(final String file, final Object context) {
        if (wrapped == null) {
            super.checkRead(file, context);
        } else {
            wrapped.checkRead(file, context);
        }
    }

    @Override
    public void checkSecurityAccess(final String target) {
        if (wrapped == null) {
            super.checkSecurityAccess(target);
        } else {
            wrapped.checkSecurityAccess(target);
        }
    }

    @Override
    public void checkSetFactory() {
        if (wrapped == null) {
            super.checkSetFactory();
        } else {
            wrapped.checkSetFactory();
        }
    }

    @Override
    public void checkSystemClipboardAccess() {
        if (wrapped == null) {
            super.checkSystemClipboardAccess();
        } else {
            wrapped.checkSystemClipboardAccess();
        }
    }

    @Override
    public boolean checkTopLevelWindow(final Object window) {
        if (wrapped == null)
            return super.checkTopLevelWindow(window);
        return wrapped.checkTopLevelWindow(window);
    }

    @Override
    public void checkWrite(final FileDescriptor fd) {
        if (wrapped == null) {
            super.checkWrite(fd);
        } else {
            wrapped.checkWrite(fd);
        }
    }

    @Override
    public void checkWrite(final String file) {
        if (wrapped == null) {
            super.checkWrite(file);
        } else {
            wrapped.checkWrite(file);
        }
    }

    @Deprecated
    @Override
    public boolean getInCheck() {
        if (wrapped == null)
            return super.getInCheck();
        return wrapped.getInCheck();
    }

    @Override
    public Object getSecurityContext() {
        if (wrapped == null)
            return super.getSecurityContext();
        return wrapped.getSecurityContext();
    }

    @Override
    public ThreadGroup getThreadGroup() {
        if (wrapped == null)
            return super.getThreadGroup();
        return wrapped.getThreadGroup();
    }

    public SecurityManager getWrapped() {
        return wrapped;
    }

    public void setWrapped(final SecurityManager wrapped) {
        this.wrapped = wrapped;
    }
}
