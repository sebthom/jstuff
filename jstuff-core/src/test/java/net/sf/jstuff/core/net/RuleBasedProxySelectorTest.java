/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.net;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RuleBasedProxySelectorTest {

   @Test
   public void testRuleBasedProxySelector() throws URISyntaxException {
      final var selector = new RuleBasedProxySelector(false);
      selector.addProxyRule(new Proxy(Type.HTTP, new InetSocketAddress("daproxy", 8080)), ".*//example.com/.*");

      assertThat(selector.select(new URI("https://foobar.com/entry"))).isEmpty();
      assertThat(selector.select(new URI("https://example.com/entry"))).hasSize(1) //
         .anyMatch(p -> ((InetSocketAddress) asNonNull(p.address())).getHostName().equals("daproxy"));
   }
}
