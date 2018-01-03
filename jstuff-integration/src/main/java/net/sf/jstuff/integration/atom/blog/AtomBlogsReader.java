/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.atom.blog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.jstuff.xml.StAXUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlogsReader {
    protected static final class AtomCollection {
        protected String accept;
        protected String href;
    }

    private static final ThreadLocal<XMLInputFactory> XML_INPUT_FACTORY = new ThreadLocal<XMLInputFactory>() {
        @Override
        protected XMLInputFactory initialValue() {
            return XMLInputFactory.newInstance();
        };
    };

    private static AtomCollection processCollection(final XMLStreamReader xmlr) throws XMLStreamException {
        if (!(xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("collection")))
            return null;

        final AtomCollection coll = new AtomCollection();
        coll.href = StAXUtils.getAttributeValue(xmlr, "href");
        while (xmlr.hasNext() && !(xmlr.getEventType() == XMLStreamConstants.END_ELEMENT && xmlr.getLocalName().equals("collection"))) {
            if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("accept")) {
                coll.accept = xmlr.getElementText();
                xmlr.next();
            }
            xmlr.next();
        }
        return coll;
    }

    public static List<AtomBlog> processStream(final InputStream is, final String encoding) throws XMLStreamException {
        final XMLStreamReader xmlr = XML_INPUT_FACTORY.get().createXMLStreamReader(is, encoding);
        final List<AtomBlog> blogs = new ArrayList<AtomBlog>(2);

        while (xmlr.hasNext()) {
            xmlr.next();
            processWorkspace(xmlr, blogs);
        }
        return blogs;
    }

    private static String processTitle(final XMLStreamReader xmlr) throws XMLStreamException {
        if (!(xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("title")))
            return null;

        return xmlr.getElementText();
    }

    private static void processWorkspace(final XMLStreamReader xmlr, final List<AtomBlog> blogs) throws XMLStreamException {
        if (!(xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("workspace")))
            return;

        String title = null;
        AtomCollection entryCollection = null;

        while (xmlr.hasNext()) {
            xmlr.next();
            if (xmlr.getEventType() == XMLStreamConstants.END_ELEMENT)
                break;

            final String localTitle = processTitle(xmlr);
            if (localTitle != null)
                title = localTitle;

            final AtomCollection currentColl = processCollection(xmlr);

            //if (currentColl != null && "entry".equals(currentColl.accept))
            if (currentColl != null && "application/atom+xml; type=entry".equals(currentColl.accept)) {
                entryCollection = currentColl;
                break;
            }
        }

        if (entryCollection != null) {
            final AtomBlog blog = new AtomBlog();
            blog.setEntriesUrl(entryCollection.href);
            blog.setTitle(title);
            blogs.add(blog);
        }
    }

    protected AtomBlogsReader() {
        super();
    }
}