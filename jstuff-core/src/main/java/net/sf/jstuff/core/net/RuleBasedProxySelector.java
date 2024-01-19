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
      private static String withoutUserInfo(final URI uri) {
         final String userInfo = uri.getUserInfo();
         if (userInfo == null)
            return uri.toString();
         return uri.toString().replace(userInfo + '@', "");
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
            : withoutUserInfo(uri);
         if (urlPattern.matcher(url).matches())
            return proxy;
         return null;
      }
   }

   private static final Logger LOG = Logger.create();

   private final CopyOnWriteArrayList<ProxyRule> proxyRules = new CopyOnWriteArrayList<>();
   private final boolean fallbackToDefaultProxySelector;
   private @Nullable ProxySelector previousDefaultSelector;

   public RuleBasedProxySelector() {
      this(true);
   }

   public RuleBasedProxySelector(final boolean fallbackToDefaultProxySelector) {
      this.fallbackToDefaultProxySelector = fallbackToDefaultProxySelector;
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

   public boolean removeProxyRule(final ProxyRule rule) {
      return proxyRules.remove(rule);
   }

   @Override
   public void connectFailed(final URI uri, final SocketAddress addr, final IOException ex) {
      // ignored
   }

   /**
    * Installs this proxy selector using {@link ProxySelector#setDefault(ProxySelector)}
    * and keeps a reference to the previous default proxy selector to be used as fallback if enabled.
    *
    * @return the previously set default proxy selector
    */
   public @Nullable ProxySelector installAsDefault() {
      synchronized (RuleBasedProxySelector.class) {
         final var current = ProxySelector.getDefault();
         if (current == this)
            return previousDefaultSelector;

         previousDefaultSelector = current;
         ProxySelector.setDefault(this);
         return previousDefaultSelector;
      }
   }

   public boolean isInstalledAsDefault() {
      return ProxySelector.getDefault() == this;
   }

   public boolean uninstallAsDefault() {
      synchronized (RuleBasedProxySelector.class) {
         final var current = ProxySelector.getDefault();
         if (current == this) {
            ProxySelector.setDefault(previousDefaultSelector);
            previousDefaultSelector = null;
            return true;
         }
         return false;
      }
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
      if (fallbackToDefaultProxySelector) {
         var defaultSelector = ProxySelector.getDefault();
         if (defaultSelector == this) {
            defaultSelector = previousDefaultSelector;
         }
         if (defaultSelector != null) {
            result.addAll(defaultSelector.select(uri));
         }
      }

      return LOG.exit(result);
   }
}
