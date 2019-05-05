/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.blog;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.jstuff.xml.stream.StAXUtils;

public abstract class AtomBlogPostEntryResponseReader {
   private static final ThreadLocal<XMLInputFactory> XML_INPUT_FACTORY = new ThreadLocal<XMLInputFactory>() {
      @Override
      protected XMLInputFactory initialValue() {
         return XMLInputFactory.newInstance();
      }
   };

   private static AtomBlogEntry processEntry(final XMLStreamReader xmlr) throws XMLStreamException {
      if (xmlr.getEventType() != XMLStreamConstants.START_ELEMENT || !xmlr.getLocalName().equals("entry"))
         return null;

      final AtomBlogEntry atomBlockEntry = new AtomBlogEntry();

      while (xmlr.hasNext()) {
         xmlr.next();
         processId(xmlr, atomBlockEntry);
         processLink(xmlr, atomBlockEntry);
      }
      return atomBlockEntry;
   }

   private static void processId(final XMLStreamReader xmlr, final AtomBlogEntry atomBlogEntry) throws XMLStreamException {
      if (xmlr.getEventType() != XMLStreamConstants.START_ELEMENT || !xmlr.getLocalName().equals("id"))
         return;

      atomBlogEntry.setId(xmlr.getElementText());
   }

   private static void processLink(final XMLStreamReader xmlr, final AtomBlogEntry atomBlogEntry) {
      if (xmlr.getEventType() != XMLStreamConstants.START_ELEMENT || !xmlr.getLocalName().equals("link"))
         return;

      final String rel = StAXUtils.getAttributeValue(xmlr, "rel");
      if ("edit".equals(rel)) {
         atomBlogEntry.setEditURL(StAXUtils.getAttributeValue(xmlr, "href"));
      } else if ("alternate".equals(rel)) {
         atomBlogEntry.setDisplayURL(StAXUtils.getAttributeValue(xmlr, "href"));
      }
   }

   public static AtomBlogEntry processStream(final InputStream is, final String encoding) throws XMLStreamException {
      final XMLStreamReader xmlr = XML_INPUT_FACTORY.get().createXMLStreamReader(is, encoding);

      // Loop over XML input stream and process events
      while (xmlr.hasNext()) {
         xmlr.next();
         return processEntry(xmlr);
      }
      return null;
   }
}
