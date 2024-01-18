/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.net;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuleBasedProxySelector extends ProxySelector {

   @FunctionalInterface
   public interface ProxyRule {
      @Nullable
      Proxy getProxy(URI uri);
   }

   public static class PatternProxyRule implements ProxyRule {
      private static String toURLWithoutCreds(final URI uri) {
         final String userInfo = uri.getUserInfo();
         if (userInfo == null)
            return uri.toString();
         return uri.toString().replace(userInfo + "@", "");
      }

      private final boolean evaluateUserInfo;
      private final Proxy proxy;
      private final Pattern urlPattern;

      public PatternProxyRule(final Proxy proxy, final Pattern urlPattern) {
         this(proxy, urlPattern, false);
      }

      public PatternProxyRule(final Proxy proxy, final Pattern urlPattern, final boolean evaluateUserInfo) {
         this.proxy = proxy;
         this.urlPattern = urlPattern;
         this.evaluateUserInfo = evaluateUserInfo;
      }

      @Override
      public @Nullable Proxy getProxy(final URI uri) {
         final String url = evaluateUserInfo //
            ? uri.toString() //
            : toURLWithoutCreds(uri);
         if (urlPattern.matcher(url).matches())
            return proxy;
         return null;
      }
   }

   private static final Logger LOG = Logger.create();

   private final CopyOnWriteArrayList<ProxyRule> proxyRules = new CopyOnWriteArrayList<>();
   private final boolean includeSystemProxy;
   private @Nullable ProxySelector oldSystemSelector;

   public RuleBasedProxySelector() {
      this(true);
   }

   public RuleBasedProxySelector(final boolean includeSystemProxy) {
      this.includeSystemProxy = includeSystemProxy;
   }

   public ProxyRule addProxyRule(final Proxy proxy, final Pattern urlPattern) {
      final var rule = new PatternProxyRule(proxy, urlPattern);
      proxyRules.add(rule);
      return rule;
   }

   public ProxyRule addProxyRule(final Proxy proxy, final String urlPattern) {
      return addProxyRule(proxy, Pattern.compile(urlPattern));
   }

   public void addProxyRule(final ProxyRule rule) {
      proxyRules.add(rule);
   }

   public void clearProxyRules() {
      proxyRules.clear();
   }

   @Override
   public void connectFailed(final URI uri, final SocketAddress addr, final IOException ex) {
      // ignored
   }

   public void installSystemWide() {
      synchronized (RuleBasedProxySelector.class) {
         final var current = ProxySelector.getDefault();
         if (current == this)
            return;

         oldSystemSelector = current;
         ProxySelector.setDefault(this);
      }
   }

   public boolean removeProxyRule(final ProxyRule rule) {
      return proxyRules.remove(rule);
   }

   @Override
   public List<Proxy> select(final URI uri) {
      LOG.entry(uri);
      final var result = new ArrayList<Proxy>(4);
      for (final var rule : proxyRules) {
         try {
            final var proxy = rule.getProxy(uri);
            if (proxy != null) {
               result.add(proxy);
            }
         } catch (final Exception ex) {
            LOG.error(ex);
         }
      }
      if (includeSystemProxy) {
         final var systemSelector = ProxySelector.getDefault();
         if (systemSelector == this) {
            final var oldDefaultSelector = oldSystemSelector;
            if (oldDefaultSelector != null) {
               result.addAll(oldDefaultSelector.select(uri));
            }
         } else {
            result.addAll(systemSelector.select(uri));
         }
      }

      return LOG.exit(result);
   }
}
