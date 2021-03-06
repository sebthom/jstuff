/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.blog;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlogUtils {
   private static final ThreadLocal<XMLOutputFactory> XML_OUTPUT_FACTORY = ThreadLocal.withInitial(XMLOutputFactory::newInstance);

   /**
    * @param blogEntryEditURL e.g. http://myserver/weblogs/services/atom/me@acme.com/entries/24CG0902859327955BED3587DC5B5A0003E8
    */
   public static void deleteBlogEntry(final String blogEntryEditURL, final String logonName, final String password)
      throws DeletingAtomBlogEntryFailedException {
      try {
         final Credentials credentials = new UsernamePasswordCredentials(logonName, password);

         final HttpClient c = getHttpClient(credentials);

         final DeleteMethod deleteEntry = new DeleteMethod(blogEntryEditURL);
         deleteEntry.setDoAuthentication(true);
         try {
            final int httpStatus = c.executeMethod(deleteEntry);

            final String response = deleteEntry.getResponseBodyAsString();

            if (httpStatus < 200 || httpStatus >= 300)
               throw new DeletingAtomBlogEntryFailedException("Deleting blog entry failed: HTTP Status Code " + httpStatus + "\n" + response);

         } finally {
            // release any connection resources used by the method
            deleteEntry.releaseConnection();
         }
      } catch (final DeletingAtomBlogEntryFailedException ex) {
         throw ex;
      } catch (final Exception ex) {
         throw new DeletingAtomBlogEntryFailedException("Deleting blog entry failed: " + ex.getMessage(), ex);
      }
   }

   public static void enableHttpClientLogging() {
      System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
      System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
      System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
      System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.content", "debug");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
   }

   public static List<AtomBlog> getBlogs(final String blogAtomServiceURL, final String logonName, final String password)
      throws ReceivingAtomBlogsFailedException {
      final Credentials credentials = new UsernamePasswordCredentials(logonName, password);
      final HttpClient c = getHttpClient(credentials);

      final GetMethod getBlogs = new GetMethod(blogAtomServiceURL);

      try {
         try {
            getBlogs.setDoAuthentication(true);
            getBlogs.addRequestHeader("Content-Type", "application/atom+xml;type=entry");

            final int httpStatus = c.executeMethod(getBlogs);

            try (InputStream responseBodyStream = getBlogs.getResponseBodyAsStream()) {
               if (httpStatus < 200 || httpStatus >= 300) {
                  final String response = IOUtils.toString(responseBodyStream, getBlogs.getResponseCharSet());
                  throw new ReceivingAtomBlogsFailedException("Receiving atom blogs failed with: HTTP Status Code " + httpStatus + "\n" + response);
               }

               return AtomBlogsReader.processStream(responseBodyStream, getBlogs.getResponseCharSet());
            }
         } finally {
            getBlogs.releaseConnection();
         }
      } catch (final ReceivingAtomBlogsFailedException ex) {
         throw ex;
      } catch (final Exception ex) {
         throw new ReceivingAtomBlogsFailedException("Receiving atom blogs failed with: " + ex.getMessage(), ex);
      }
   }

   private static HttpClient getHttpClient(final Credentials credentials) {
      final HttpClient c = new HttpClient();
      c.getParams().setParameter("http.useragent", AtomBlogUtils.class.getName());
      c.getParams().setAuthenticationPreemptive(true);
      c.getParams().setSoTimeout(5000);
      c.getParams().setConnectionManagerTimeout(5000);
      c.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, 80, AuthScope.ANY_REALM), credentials);
      return c;
   }

   /**
    * @param blogAtomServiceEntriesURL e.g. http://myserver/weblogs/services/atom/blueComment/entries
    */
   public static void postBlogEntry(final AtomBlogEntry atomBlogEntry, final String blogAtomServiceEntriesURL, final String logonName, final String password)
      throws PublishingAtomBlogEntryFailedException {
      try {
         final StringWriter entryAsXML = new StringWriter();
         final XMLStreamWriter staxWriter = XML_OUTPUT_FACTORY.get().createXMLStreamWriter(entryAsXML);
         staxWriter.writeStartDocument();
         {
            staxWriter.writeStartElement("entry");
            staxWriter.writeNamespace("", "http://www.w3.org/2005/Atom");
            staxWriter.writeNamespace("app", "http://purl.org/atom/app#");
            {
               staxWriter.writeStartElement("id");
               staxWriter.writeCharacters("urn:uuid:" + UUID.randomUUID().toString().replace("-", "").toUpperCase());
               staxWriter.writeEndElement();
            }
            {
               staxWriter.writeStartElement("title");
               staxWriter.writeAttribute("type", "text");
               staxWriter.writeCharacters(atomBlogEntry.getTitle());
               staxWriter.writeEndElement();
            }
            {
               staxWriter.writeStartElement("app", "control", "http://purl.org/atom/app#");
               {
                  staxWriter.writeStartElement("app", "draft", "http://purl.org/atom/app#");
                  staxWriter.writeCharacters("yes");
                  staxWriter.writeEndElement();
               }
               staxWriter.writeEndElement();
            }
            {
               if (atomBlogEntry.getTags() != null) {
                  for (final String category : atomBlogEntry.getTags()) {
                     staxWriter.writeStartElement("category");
                     staxWriter.writeAttribute("term", category);
                     staxWriter.writeEndElement();
                  }
               }
            }
            {
               staxWriter.writeStartElement("content");
               staxWriter.writeAttribute("type", "html");
               staxWriter.writeCharacters(atomBlogEntry.getBody());
               staxWriter.writeEndElement();
            }
            staxWriter.writeEndElement();
         }
         staxWriter.writeEndDocument();

         final Credentials credentials = new UsernamePasswordCredentials(logonName, password);

         final HttpClient c = getHttpClient(credentials);

         final PostMethod postEntry = new PostMethod(blogAtomServiceEntriesURL);
         postEntry.setDoAuthentication(true);
         postEntry.addRequestHeader("Content-Type", "application/atom+xml;type=entry");
         postEntry.setRequestEntity(new StringRequestEntity(entryAsXML.toString(), null, null));
         try {
            final int httpStatus = c.executeMethod(postEntry);
            try (InputStream responseBodyStream = postEntry.getResponseBodyAsStream()) {
               if (httpStatus < 200 || httpStatus >= 300) {
                  final String response = IOUtils.toString(responseBodyStream, postEntry.getResponseCharSet());
                  throw new PublishingAtomBlogEntryFailedException("Publishing atom blog entry failed with: HTTP Status Code " + httpStatus + "\n" + response);
               }

               final AtomBlogEntry responseAtomBlogEntry = AtomBlogPostEntryResponseReader.processStream(responseBodyStream, postEntry.getResponseCharSet());

               if (responseAtomBlogEntry.getId() == null)
                  throw new PublishingAtomBlogEntryFailedException("Publishing atom blog entry failed with: No blog entry ID received.");

               atomBlogEntry.setId(responseAtomBlogEntry.getId());
               atomBlogEntry.setDisplayURL(responseAtomBlogEntry.getDisplayURL());
               atomBlogEntry.setEditURL(responseAtomBlogEntry.getEditURL());
            }
         } finally {
            // release any connection resources used by the method
            postEntry.releaseConnection();
         }
      } catch (final PublishingAtomBlogEntryFailedException ex) {
         throw ex;
      } catch (final Exception ex) {
         throw new PublishingAtomBlogEntryFailedException("Publishing atom blog entry failed with: " + ex.getMessage(), ex);
      }
   }

   protected AtomBlogUtils() {
   }
}
