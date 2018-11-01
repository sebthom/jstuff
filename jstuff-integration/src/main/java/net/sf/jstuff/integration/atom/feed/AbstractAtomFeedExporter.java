/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.remoting.support.RemotingSupport;
import org.springframework.web.HttpRequestHandler;

import com.thoughtworks.xstream.XStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.date.Dates;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractAtomFeedExporter extends RemotingSupport implements HttpRequestHandler {
   private static final FastDateFormat DF = FastDateFormat.getInstance("yyyy-MM-dd");

   private final XStream xstream = new XStream();
   private final String feedAuthorName = "unknown";
   private final String feedAuthorEMail = "unknown";
   private final String feedId = "entries";
   private final String feedTitle = "Recent entries";
   private final String feedSubTitle = "";

   public AbstractAtomFeedExporter() {
      xstream.registerConverter(new AtomTextConverter());
      xstream.processAnnotations(AtomFeed.class);
      xstream.processAnnotations(AtomLink.class);
      xstream.processAnnotations(AtomPerson.class);
      xstream.processAnnotations(AtomEntry.class);
   }

   protected AtomEntry[] getEntries(final HttpServletRequest request) {
      final List<AtomEntry> entries = newArrayList();

      for (final SimpleEntry<?> se : getSimpleEntries(request)) {
         final AtomEntry entry = new AtomEntry();
         entry.setId("tag:" + request.getServerName() + "," + DF.format(se.getDateCreated()) + ":" + feedId + "/" + se.getId());
         entry.setTitle(se.getSubject());
         //entry.setLink(new AtomLink("TODO", AtomLink.REL_RELATED, null));
         entry.setSummary(new AtomText(Strings.htmlToPlainText(Strings.nullToEmpty(se.getContent())).toString()));
         entry.setContent(new AtomText(se.getContent(), se.getContent().indexOf('<') > -1 ? AtomText.TYPE_HTML : AtomText.TYPE_TEXT));
         entry.setPublished(se.getDateCreated());
         entry.setUpdated(se.getDateLastModified() == null ? se.getDateCreated() : se.getDateLastModified());
         entry.setAuthor(new AtomPerson(se.getAuthorDisplayName(), se.getAuthorEMailAddress(), se.getAuthorURL()));
         final String tags = se.getTags();
         if (!Strings.isBlank(tags)) {
            final String[] tagsArray = Strings.split(tags);
            final AtomCategory[] cats = new AtomCategory[tagsArray.length];
            for (int i = 0, l = tagsArray.length; i < l; i++) {
               cats[i] = new AtomCategory(tagsArray[i]);
            }
            entry.setCategories(cats);
         }
         entries.add(entry);
      }
      return entries.toArray(new AtomEntry[entries.size()]);
   }

   /**
    * @return the feedAuthorEMail
    */
   public String getFeedAuthorEMail() {
      return feedAuthorEMail;
   }

   /**
    * @return the feedAuthorName
    */
   public String getFeedAuthorName() {
      return feedAuthorName;
   }

   /**
    * @return the feedId
    */
   public String getFeedId() {
      return feedId;
   }

   /**
    * @return the feedSubTitle
    */
   public String getFeedSubTitle() {
      return feedSubTitle;
   }

   /**
    * @return the feedTitle
    */
   public String getFeedTitle() {
      return feedTitle;
   }

   protected String getLogoURL(final HttpServletRequest request) {
      return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/images/logo.png";
   }

   protected abstract List<SimpleEntry<?>> getSimpleEntries(HttpServletRequest request);

   public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
      final AtomFeed atomFeed = new AtomFeed("tag:" + request.getServerName() + "," + Dates.getCurrentYear() + ":" + feedId);
      atomFeed.setLogo(getLogoURL(request));
      atomFeed.setTitle(feedTitle);
      atomFeed.setSubtitle(feedSubTitle);
      atomFeed.setLinks(new AtomLink(request.getRequestURL().toString(), AtomLink.REL_SELF, AtomLink.TYPE_APPLICATION_ATOM_XML));
      atomFeed.setUpdated(new Date());
      atomFeed.setAuthors(new AtomPerson(feedAuthorName, feedAuthorEMail, request.getContextPath()));

      atomFeed.setEntries(getEntries(request));

      response.setCharacterEncoding("UTF-8");
      //response.setContentType("text/xml; charset=UTF-8");
      response.setContentType("application/atom+xml; charset=UTF-8");

      @SuppressWarnings("resource")
      final ServletOutputStream out = response.getOutputStream();
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      xstream.toXML(atomFeed, out);
      out.flush();
   }
}
