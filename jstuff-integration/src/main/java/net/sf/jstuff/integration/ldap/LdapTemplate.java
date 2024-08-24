/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.ldap;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import net.sf.jstuff.core.functional.Invocable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapTemplate {
   private String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
   private Hashtable<String, Object> ldapSettings = lateNonNull();
   private String ldapURL = lateNonNull();
   private boolean pooled = true;

   /**
    * https://docs.oracle.com/javase/jndi/tutorial/ldap/ext/starttls.html
    */
   private boolean useStartTSL = false;

   @NonNullByDefault({})
   public <T> T execute(final @NonNull Invocable<T, @NonNull LdapContext, ? extends Exception> callback) {
      LdapContext ctx = null;
      StartTlsResponse tls = null;
      try {
         ctx = new InitialLdapContext(ldapSettings, null);
         if (useStartTSL) {
            tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
            tls.negotiate();
         }
         return callback.invoke(ctx);
      } catch (final Exception ex) {
         throw new LdapException(ex);
      } finally {
         LdapUtils.closeQuietly(tls);
         LdapUtils.closeQuietly(ctx);
      }
   }

   public String getInitialContextFactory() {
      return initialContextFactory;
   }

   public String getLdapURL() {
      return ldapURL;
   }

   @PostConstruct
   public void initialize() {
      ldapSettings = new Hashtable<>();
      ldapSettings.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
      ldapSettings.put(Context.PROVIDER_URL, ldapURL);
      ldapSettings.put(Context.REFERRAL, "throw");
      if (pooled) {
         ldapSettings.put("com.sun.jndi.ldap.connect.pool", "true");
      }
   }

   public boolean isPooled() {
      return pooled;
   }

   public boolean isUseStartTSL() {
      return useStartTSL;
   }

   public void setInitialContextFactory(final String initialContextFactory) {
      this.initialContextFactory = initialContextFactory;
   }

   @Inject
   public void setLdapURL(final String ldapURL) {
      this.ldapURL = ldapURL;
   }

   public void setPooled(final boolean pooled) {
      this.pooled = pooled;
   }

   public void setUseStartTSL(final boolean useStartTSL) {
      this.useStartTSL = useStartTSL;
   }
}
