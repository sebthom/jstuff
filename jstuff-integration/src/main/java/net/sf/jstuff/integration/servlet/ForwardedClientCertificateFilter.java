package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.Loops;
import net.sf.jstuff.core.io.CharEncoding;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.security.Base64;
import net.sf.jstuff.core.security.x509.X509Utils;

/**
 * A servlet {@link Filter} that transforms the {@code "X-Forwarded-Client-Cert"} HTTP header to the {@code "javax.servlet.request.X509Certificate"} Servlet
 * attribute.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ForwardedClientCertificateFilter implements Filter {

   private static final Logger LOG = Logger.create();

   public static final String SERVLET_ATTRIBUTE = "javax.servlet.request.X509Certificate";
   public static final String REQUEST_HEADER = "X-Forwarded-Client-Cert";

   public ForwardedClientCertificateFilter() {
      LOG.infoNew(this);
   }

   @Override
   public void destroy() {
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
         ServletException {
      if (request instanceof final HttpServletRequest httpReq) {
         try {
            final var certsEncoded = new ArrayList<String>();

            Loops.forEach(httpReq.getHeaders(REQUEST_HEADER), header -> {
               if (Strings.isBlank(header))
                  return;
               header = header.trim();
               if (Strings.contains(header, ',')) {
                  CollectionUtils.addAll(certsEncoded, Strings.split(header, ','));
               } else {
                  certsEncoded.add(header);
               }
            });

            if (!certsEncoded.isEmpty()) {
               final var certs = new ArrayList<X509Certificate>(certsEncoded.size());

               for (final String certEncoded : certsEncoded) {
                  byte[] certDecoded = null;
                  try {
                     certDecoded = Base64.decode(certEncoded);
                  } catch (final Exception ex) {
                     LOG.debug(ex);
                     certDecoded = URLDecoder.decode(certEncoded, CharEncoding.UTF_8.charset).getBytes();
                  }
                  certs.add(X509Utils.getCertificate(certDecoded));
               }
               if (!certs.isEmpty()) {
                  httpReq.setAttribute(SERVLET_ATTRIBUTE, certs.toArray(new X509Certificate[certs.size()]));
               }
            }
         } catch (final Exception ex) {
            LOG.error("Parsing certificates of HTTP header '" + REQUEST_HEADER + "' failed.", ex);
         }
      }

      chain.doFilter(request, response);
   }

   @Override
   public void init(final FilterConfig filterConfig) {
   }

}
