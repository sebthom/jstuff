/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.atom.blog;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.jstuff.xml.StAXUtils;

public class AtomBlogPostEntryResponseReader
{
	private static final ThreadLocal<XMLInputFactory> XML_INPUT_FACTORY = new ThreadLocal<XMLInputFactory>()
		{
			@Override
			protected XMLInputFactory initialValue()
			{
				return XMLInputFactory.newInstance();
			};
		};

	private static AtomBlogEntry processEntry(final XMLStreamReader xmlr) throws XMLStreamException
	{
		if (!(xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("entry"))) return null;

		final AtomBlogEntry atomBlockEntry = new AtomBlogEntry();

		while (xmlr.hasNext())
		{
			xmlr.next();
			processId(xmlr, atomBlockEntry);
			processLink(xmlr, atomBlockEntry);
		}
		return atomBlockEntry;
	}

	private static void processId(final XMLStreamReader xmlr, final AtomBlogEntry atomBlogEntry) throws XMLStreamException
	{
		if (!(xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("id"))) return;

		atomBlogEntry.setId(xmlr.getElementText());
	}

	private static void processLink(final XMLStreamReader xmlr, final AtomBlogEntry atomBlogEntry)
	{
		if (!(xmlr.getEventType() == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("link"))) return;

		final String rel = StAXUtils.getAttributeValue(xmlr, "rel");
		if (rel.equals("edit"))
			atomBlogEntry.setEditURL(StAXUtils.getAttributeValue(xmlr, "href"));
		else if (rel.equals("alternate")) atomBlogEntry.setDisplayURL(StAXUtils.getAttributeValue(xmlr, "href"));
	}

	public static AtomBlogEntry processStream(final InputStream is, final String encoding) throws XMLStreamException
	{
		final XMLStreamReader xmlr = XML_INPUT_FACTORY.get().createXMLStreamReader(is, encoding);

		// Loop over XML input stream and process events
		while (xmlr.hasNext())
		{
			xmlr.next();
			return processEntry(xmlr);
		}
		return null;
	}
}
