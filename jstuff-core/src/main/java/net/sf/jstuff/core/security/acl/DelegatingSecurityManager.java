/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.acl;

import java.io.FileDescriptor;
import java.lang.reflect.Member;
import java.net.InetAddress;
import java.security.Permission;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * Security manager that delegates all method invocations to the wrapped security manager instance.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingSecurityManager extends NoOpSecurityManager {

   @Nullable
   private SecurityManager wrapped;

   public DelegatingSecurityManager() {
      this(null);
   }

   public DelegatingSecurityManager(final @Nullable SecurityManager wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public void checkAccept(final String host, final int port) {
      if (wrapped != null) {
         wrapped.checkAccept(host, port);
      } else {
         super.checkAccept(host, port);
      }
   }

   @Override
   public void checkAccess(final Thread t) {
      if (wrapped != null) {
         wrapped.checkAccess(t);
      } else {
         super.checkAccess(t);
      }
   }

   @Override
   public void checkAccess(final ThreadGroup g) {
      if (wrapped != null) {
         wrapped.checkAccess(g);
      } else {
         super.checkAccess(g);
      }
   }

   /**
    * Removed in JDK9
    */
   @Deprecated
   public void checkAwtEventQueueAccess() {
      checkPermission(new java.awt.AWTPermission("accessEventQueue"));
   }

   @Override
   public void checkConnect(final String host, final int port) {
      if (wrapped != null) {
         wrapped.checkConnect(host, port);
      } else {
         super.checkConnect(host, port);
      }
   }

   @Override
   public void checkConnect(final String host, final int port, final Object context) {
      if (wrapped != null) {
         wrapped.checkConnect(host, port, context);
      } else {
         super.checkConnect(host, port, context);
      }
   }

   @Override
   public void checkCreateClassLoader() {
      if (wrapped != null) {
         wrapped.checkCreateClassLoader();
      } else {
         super.checkCreateClassLoader();
      }
   }

   @Override
   public void checkDelete(final String file) {
      if (wrapped != null) {
         wrapped.checkDelete(file);
      } else {
         super.checkDelete(file);
      }
   }

   @Override
   public void checkExec(final String cmd) {
      if (wrapped != null) {
         wrapped.checkExec(cmd);
      } else {
         super.checkExec(cmd);
      }
   }

   @Override
   public void checkExit(final int status) {
      if (wrapped != null) {
         wrapped.checkExit(status);
      } else {
         super.checkExit(status);
      }
   }

   @Override
   public void checkLink(final String lib) {
      if (wrapped != null) {
         wrapped.checkLink(lib);
      } else {
         super.checkLink(lib);
      }
   }

   @Override
   public void checkListen(final int port) {
      if (wrapped != null) {
         wrapped.checkListen(port);
      } else {
         super.checkListen(port);
      }
   }

   /**
    * Removed in JDK9
    */
   @Deprecated
   public void checkMemberAccess(final Class<?> clazz, final int which) {
      Args.notNull("clazz", clazz);
      if (which != Member.PUBLIC) {
         final Class<?>[] stack = getClassContext();
         if (stack.length < 4 || stack[3].getClassLoader() != clazz.getClassLoader()) {
            checkPermission(new RuntimePermission("accessDeclaredMembers"));
         }
      }
   }

   @Override
   public void checkMulticast(final InetAddress maddr) {
      if (wrapped != null) {
         wrapped.checkMulticast(maddr);
      } else {
         super.checkMulticast(maddr);
      }
   }

   @Deprecated
   @Override
   public void checkMulticast(final InetAddress maddr, final byte ttl) {
      if (wrapped != null) {
         wrapped.checkMulticast(maddr, ttl);
      } else {
         super.checkMulticast(maddr, ttl);
      }
   }

   @Override
   public void checkPackageAccess(final String pkg) {
      if (wrapped != null) {
         wrapped.checkPackageAccess(pkg);
      } else {
         super.checkPackageAccess(pkg);
      }
   }

   @Override
   public void checkPackageDefinition(final String pkg) {
      if (wrapped != null) {
         wrapped.checkPackageDefinition(pkg);
      } else {
         super.checkPackageDefinition(pkg);
      }
   }

   @Override
   public void checkPermission(final Permission perm) {
      if (wrapped != null) {
         wrapped.checkPermission(perm);
      } else {
         super.checkPermission(perm);
      }
   }

   @Override
   public void checkPermission(final Permission perm, final Object context) {
      if (wrapped != null) {
         wrapped.checkPermission(perm, context);
      } else {
         super.checkPermission(perm, context);
      }
   }

   @Override
   public void checkPrintJobAccess() {
      if (wrapped != null) {
         wrapped.checkPrintJobAccess();
      } else {
         super.checkPrintJobAccess();
      }
   }

   @Override
   public void checkPropertiesAccess() {
      if (wrapped != null) {
         wrapped.checkPropertiesAccess();
      } else {
         super.checkPropertiesAccess();
      }
   }

   @Override
   public void checkPropertyAccess(final String key) {
      if (wrapped != null) {
         wrapped.checkPropertyAccess(key);
      } else {
         super.checkPropertyAccess(key);
      }
   }

   @Override
   public void checkRead(final FileDescriptor fd) {
      if (wrapped != null) {
         wrapped.checkRead(fd);
      } else {
         super.checkRead(fd);
      }
   }

   @Override
   public void checkRead(final String file) {
      if (wrapped != null) {
         wrapped.checkRead(file);
      } else {
         super.checkRead(file);
      }
   }

   @Override
   public void checkRead(final String file, final Object context) {
      if (wrapped != null) {
         wrapped.checkRead(file, context);
      } else {
         super.checkRead(file, context);
      }
   }

   @Override
   public void checkSecurityAccess(final String target) {
      if (wrapped != null) {
         wrapped.checkSecurityAccess(target);
      } else {
         super.checkSecurityAccess(target);
      }
   }

   @Override
   public void checkSetFactory() {
      if (wrapped != null) {
         wrapped.checkSetFactory();
      } else {
         super.checkSetFactory();
      }
   }

   /**
    * Removed in JDK9
    */
   @Deprecated
   public void checkSystemClipboardAccess() {
      checkPermission(new java.awt.AWTPermission("accessClipboard"));
   }

   /**
    * Removed in JDK9
    */
   @Deprecated
   public boolean checkTopLevelWindow(final Object window) {
      Args.notNull("window", window);
      try {
         checkPermission(new java.awt.AWTPermission("showWindowWithoutWarningBanner"));
         return true;
      } catch (final SecurityException ex) {
         return false;
      }
   }

   @Override
   public void checkWrite(final FileDescriptor fd) {
      if (wrapped != null) {
         wrapped.checkWrite(fd);
      } else {
         super.checkWrite(fd);
      }
   }

   @Override
   public void checkWrite(final String file) {
      if (wrapped != null) {
         wrapped.checkWrite(file);
      } else {
         super.checkWrite(file);
      }
   }

   /**
    * Removed in JDK9
    */
   @Deprecated
   public boolean getInCheck() {
      return false;
   }

   @Override
   public Object getSecurityContext() {
      if (wrapped != null)
         return wrapped.getSecurityContext();
      return super.getSecurityContext();
   }

   @Override
   public ThreadGroup getThreadGroup() {
      if (wrapped != null)
         return wrapped.getThreadGroup();
      return super.getThreadGroup();
   }

   @Nullable
   public SecurityManager getWrapped() {
      return wrapped;
   }

   public void setWrapped(final @Nullable SecurityManager wrapped) {
      this.wrapped = wrapped;
   }
}
